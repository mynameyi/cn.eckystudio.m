package cn.eckystudio.m.log;

public class Log {
	
	/**
	 * indicate whether enable log
	 */
	public static boolean GLOBAL_ENABLE_LOG = true;
	/***
	 * log tag
	 */
	public static String GLOBAL_LOG_TAG = "?=>liangyi";
	/**
	 * whether export the log
	 */
	public static boolean GLOBAL_EXPORT_LOG = false;
	
	public static int GLOBAL_DEFAULT_TRACE_DEPTH = 5;//default trace depth
	/**
	 * print general message
	 * @param args
	 */
	public static void print(Object... args){
		print(GLOBAL_LOG_TAG,args);
	}

	/***
	 * print log by class name
	 * @param cls
	 * @param args
	 */
	public static void print(Class cls, Object... args){
		print(cls.getSimpleName(),args);
	}

	/***
	 *
 	 * @param tag
	 * @param args
	 */
	public static void print(String tag,Object... args)
	{
		if(!GLOBAL_ENABLE_LOG)
			return;
		
	    if(args == null)
	    	return;
	    
	    String msg = "";
		for(int i=0;i<args.length;i++)
		{
			msg += args[i] + ",";
		}
		msg += " <<< Current Thread Id is " + Thread.currentThread().getId() + ">>>";
		android.util.Log.i(tag, msg);
	}
	
	public static void trace(Object... objs)
	{
		if(!GLOBAL_ENABLE_LOG)
			return;
		
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		
		String msg = "<<<Current Thread Id is " + Thread.currentThread().getId() + ">>>\r\n";
		for(int i=3;i<GLOBAL_DEFAULT_TRACE_DEPTH + 3;i++)
		{
			msg += elements[i].getFileName() +"("  + elements[i].getLineNumber()+")" + "," + elements[i].getClassName()+"."+elements[i].getMethodName()+"\r\n";
		}
		
		if(objs != null)
		{
			;
		}
		android.util.Log.i(GLOBAL_LOG_TAG, msg);
	}
	
	public static void dissect(Object obj)
	{
		if(!GLOBAL_ENABLE_LOG)
			return;
		;
	}
}
