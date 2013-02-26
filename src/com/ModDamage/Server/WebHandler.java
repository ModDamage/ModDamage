package com.ModDamage.Server;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.ModDamage.NanoHTTPD.Response;

public abstract class WebHandler
{
	public abstract Response handle(Response res, String uri, String method, Properties header, Properties parms, Properties files);
	
	protected final Response write(Response res, final WebWriter writer) {
		PipedInputStream in = new PipedInputStream();
		final PipedOutputStream out;
		try
		{
			out = new PipedOutputStream(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return res;
		}
		
		
		MDServer.executor.execute(new Runnable() {
			public void run()
			{
				PrintWriter w = new PrintWriter(out);
				try
				{
					writer.write(w);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally {
					w.close();
					try
					{
						out.close();
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		res.data = in;
		
		return res;
	}
}
