package com.artagon.xacml.v3.spi.function;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.artagon.xacml.v3.FunctionSpec;
import com.artagon.xacml.v3.spi.FunctionProvider;
import com.google.common.base.Preconditions;

public class AggregatingFunctionProvider implements FunctionProvider
{
	private Map<String, FunctionProvider> functions;
	
	public AggregatingFunctionProvider(FunctionProvider ...providers){
		this(Arrays.asList(providers));
	}
	
	/**
	 * Creates aggregating function provider with a given providers
	 * 
	 * @param providers a collection  of {@link FunctionProvider} instances
	 */
	public AggregatingFunctionProvider(Collection<FunctionProvider> providers){
		this.functions = new ConcurrentHashMap<String, FunctionProvider>();
		for(FunctionProvider p : providers){
			add(p);
		}
	}
	
	/**
	 * Adds {@link FunctionProvider} to this aggregating provider
	 * 
	 * @param provider a provider instance
	 * @exception IllegalArgumentException if function exported via
	 * given provider already exported via provider previously registered
	 * with this aggregating provider
	 */
	public void add(FunctionProvider provider)
	{
		Preconditions.checkNotNull(provider);
		for(String functionId : provider.getProvidedFunctions()){
			if(functions.containsKey(functionId)){
				throw new IllegalArgumentException(String.format("Function provider " +
						"already contains a function with functionId=\"%s\"", 
						functionId));
			}
			FunctionSpec spec = provider.getFunction(functionId);
			Preconditions.checkArgument(spec != null);
			this.functions.put(functionId, provider);
		}
	}
	
	/**
	 * Sets additional functions to this provider
	 * 
	 * @param extensions an extension functions
	 */
	public void setFunctionProviders(Collection<FunctionProvider> extensions)
	{
		for(FunctionProvider p : extensions){
			add(p);
		}
	}
	
	@Override
	public FunctionSpec getFunction(String functionId) {
		FunctionProvider provider = functions.get(functionId);
		return (provider != null)?provider.getFunction(functionId):null;
	}

	@Override
	public Iterable<String> getProvidedFunctions() {
		return  Collections.unmodifiableCollection(functions.keySet());
	}

	@Override
	public boolean isFunctionProvided(String functionId) {
		return functions.containsKey(functionId);
	}
	
}
