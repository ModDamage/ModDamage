package com.ModDamage.Backend.gen;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.RETURN;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.util.CheckClassAdapter;

import com.ModDamage.Backend.ReflectionMagic;


public class RegisterClassVisitor extends ClassVisitor
{
	final List<ParserSpec> parsers;
	
	public RegisterClassVisitor(ClassVisitor cv, final String newIName, List<ParserSpec> parsers)
	{
		super(Opcodes.ASM4, new RemappingClassAdapter(cv, new Remapper()
			{
				@Override
				public String map(String typeName)
				{
					if (typeName.endsWith("Register$template"))
						return newIName;
					return typeName;
				}
			}));
		
		this.parsers = parsers;
	}
	
	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature, String[] exceptions)
	{
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("register"))
		{
			return new MethodVisitor(api, mv)
				{
					GeneratorAdapter mg = new GeneratorAdapter(mv, access, name, desc);
					
					@Override
					public void visitInsn(int opcode)
					{
						// The only instruction here is RETURN
						
						for (ParserSpec spec : parsers)
						{
							String registerDesc = "(Ljava/lang/Class;Ljava/lang/Class;Ljava/util/regex/Pattern;Lcom/ModDamage/EventInfo/DataProvider$IDataParser;)V";
							mg.push(spec.provides);
							
							if (spec.startsWith == null)
							{
								registerDesc = "(Ljava/lang/Class;Ljava/util/regex/Pattern;Lcom/ModDamage/EventInfo/DataProvider$BaseDataParser;)V";
							}
							else
								mg.push(spec.startsWith);
							
							mg.push(spec.pattern);
							mg.push(Pattern.CASE_INSENSITIVE);
							mg.visitMethodInsn(INVOKESTATIC, "java/util/regex/Pattern", "compile", "(Ljava/lang/String;I)Ljava/util/regex/Pattern;");
							
							mg.newInstance(spec.parserType);
							mg.dup();
							mg.visitMethodInsn(Opcodes.INVOKESPECIAL, spec.parserType.getInternalName(), "<init>", "()V");
							
							mg.visitMethodInsn(INVOKESTATIC, "com/ModDamage/EventInfo/DataProvider", "register", registerDesc);
						}
						mg.visitInsn(RETURN);
					}
					
					@Override
					public void visitMaxs(int maxStack, int maxLocals)
					{
						super.visitMaxs(5, 0);
					}
				};
		}
		return mv;
	}
	

	
	public static void createRegisterClass(List<ParserSpec> parsers)
	{
		ClassReader cr;
		InputStream is;
		try
		{
			is = Class.forName("com.ModDamage.Backend.gen.RegisterClassVisitor").getResourceAsStream("RegisterClassVisitor$Register$template.class");
		}
		catch (ClassNotFoundException e1)
		{
			e1.printStackTrace();
			return;
		}
		if (is == null) return;
		try
		{
			cr = new ClassReader(is);
		}
		catch (Exception e) {
			System.err.println("REGISTER "+e); return; }
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
		
		RegisterClassVisitor rcv = new RegisterClassVisitor(new CheckClassAdapter(cw), ReflectionMagic.parentIName + "$register", parsers);
		
		cr.accept(rcv, 0);
		
		ReflectionMagic.registerClass(ReflectionMagic.parentName + "$register", cw.toByteArray());
	}



	static class Register$template
	{
		public static void register()
		{
			//DataProvider.register(null, Pattern.compile("", Pattern.CASE_INSENSITIVE), new BaseParser$template<Object>());
			
			//DataProvider.register(null, null, null, new Parser$template<Object, Object>());
			
			//DataProvider.registerTransformer(null, null, null);
		}
	}
}