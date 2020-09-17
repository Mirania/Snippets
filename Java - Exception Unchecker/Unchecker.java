public class Unchecker {
	
	//this method:
	//- uses lambdas: perform(() -> methodBody())
	//- unchecks all exceptions in methodBody
	//- removes the need for a try-catch where perform is used
	//- is not bound to any return type, so it's flexible
	public <T> T perform(Wrapper<T> wrapper) {
		try {
			return wrapper.execute();
		} catch (Exception e) {
			throw uncheck(e);
		}
	}

	//the point of this method is to make the exception unchecked without changing its class
	//it would be easy to make it unchecked by rethrowing it as a RuntimeException but that would change the class
	@SuppressWarnings("unchecked")
	private <E extends Exception> E uncheck(Exception e) throws E {
		throw (E) e;
	}
	
	//example usage
	//Files.readAllLines throws an IOException that would need to be rethrown or explicitly handled by a try-catch
	public List<String> test() {
		return perform(() -> Files.readAllLines(Paths.get("nopath")));
	}
	
}

//the wrapper class
public interface Wrapper<T> {
    T execute() throws Exception;
}