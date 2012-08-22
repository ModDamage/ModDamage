package com.ModDamage.Backend.gen;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.NEW;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.RemappingSignatureAdapter;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.ReflectionMagic;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ParserClassVisitor extends ClassVisitor
{
	final Type provides;
	final Type startType;
	
	public ParserClassVisitor(ClassVisitor cv, final String newIName, final Type provides, final Type startType)
	{
		super(Opcodes.ASM4, new RemappingClassAdapter(cv, new Remapper()
			{
				@Override
				public String map(String typeName)
				{
					if (typeName.endsWith("Parser$template"))
						return newIName;
					return typeName;
				}
				@Override
				protected SignatureVisitor createRemappingSignatureAdapter(SignatureVisitor v)
				{
			        return new RemappingSignatureAdapter(v, this) {
			        	@Override
			        	public void visitTypeVariable(String name)
			        	{
			        		if (name.equals("StartsWith")) {
			        			visitClassType(startType.getInternalName());
			        			visitEnd();
			        		}
			        		else if (name.equals("Provides")) {
			        			visitClassType(provides.getInternalName());
			        			visitEnd();
			        		}
			        		else
			        			super.visitTypeVariable(name);
			        	}
			        };
				}
			}));
		
		this.provides = provides;
		this.startType = startType;
	}
	
	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature, String[] exceptions)
	{
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("parse"))
		{
			return new MethodVisitor(api, mv) {
					@Override public void visitInsn(int opcode) {
						if (opcode == ARETURN) { super.visitInsn(opcode); return; }
						
						mv.visitTypeInsn(NEW, ReflectionMagic.providerIName);
						mv.visitInsn(DUP);
						mv.visitLdcInsn(provides);
						if (startType != null) mv.visitVarInsn(ALOAD, 3);
						else mv.visitInsn(ACONST_NULL); // push null
						mv.visitMethodInsn(INVOKESPECIAL, ReflectionMagic.providerIName, "<init>", 
								"(Ljava/lang/Class;Lcom/ModDamage/EventInfo/IDataProvider;)V");
						
						//mv.visitInsn(ARETURN);
					}
					
					@Override public void visitMaxs(int maxStack, int maxLocals) {
						super.visitMaxs(4, maxLocals);
					}
				};
		}
		return mv;
	}
	

	
	public static ParserSpec createParserClass(String name, Type provides, Type startsWith, String pattern)
	{
		ClassReader cr;
		ClassLoader cl = ModDamage.getPluginConfiguration().plugin.getClass().getClassLoader();
		String str = BaseParser$template.class.getName();
		InputStream is = cl.getResourceAsStream("com/ModDamage/Backend/gen/ParserClassVisitor"+
				"$"+(startsWith!=null?"":"Base")+"Parser$template.class");
		if (is == null) return null;
		try
		{
			cr = new ClassReader(is);
		}
		catch (Exception e) {
			System.err.println("PARSER "+e); /*e.printStackTrace();*/ return null; }
		finally {
			try
			{
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		ClassWriter cw = new ClassWriter(cr, 0);
		
		ParserClassVisitor rcv = new ParserClassVisitor(new CheckClassAdapter(cw), name, provides, startsWith);
		
		cr.accept(rcv, 0);
		
		ReflectionMagic.registerClass(ReflectionMagic.parserClassName, cw.toByteArray());
		
		return new ParserSpec(name, provides, startsWith, pattern, Type.getObjectType(ReflectionMagic.parserIName));
	}


	static class BaseParser$template<Provides> extends BaseDataParser<Provides>
	{
		@Override
		public IDataProvider<Provides> parse(EventInfo info, Class<?> want, Matcher m, StringMatcher sm)
		{
			//return new ProviderClassVisitor.Provider$template<Provides, Integer>(Integer.class, null);
			
			return null;
		}
	}
	
	static class Parser$template<Provides, StartsWith> implements IDataParser<Provides, StartsWith>
	{

		@Override
		public IDataProvider<Provides> parse(EventInfo info, Class<?> want, IDataProvider<StartsWith> startDP,
				Matcher m, StringMatcher sm)
		{
			//return new ReflectionMagic.Provider$template<Provides, StartsWith>((Class<StartsWith>) Integer.class, startDP);
			
			return null;
		}
	}
}