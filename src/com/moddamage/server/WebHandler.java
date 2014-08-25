package com.moddamage.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.regex.Matcher;

import com.moddamage.server.NanoHTTPD.Response;

public abstract class WebHandler
{
	public abstract Response handle(Response res, Matcher m, String uri, String method, Properties header, Properties parms, Properties files);
	

	protected Response send(Response res, String text)
	{
		return send(res, MDServer.HTTP_OK, MDServer.MIME_PLAINTEXT, text);
	}
	
	protected Response send(Response res, String status, String mimeType, String text)
	{
		res.status = status;
		res.mimeType = mimeType;
		
		try
		{
			res.data = new ByteArrayInputStream(text.getBytes("UTF-8"));
		}
		catch ( java.io.UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}
		
		return res;
	}
	

	protected Response send(Response res, String status, String mimeType, InputStream data)
	{
		res.status = status;
		res.mimeType = mimeType;
		res.data = data;
		
		return res;
	}

	protected final Response send(Response res, String status, String mimeType, final WebWriter writer)
	{
		res.status = status;
		res.mimeType = mimeType;
		
		return send(res, writer);
	}
	
	protected final Response send(Response res, final WebWriter writer) {
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
