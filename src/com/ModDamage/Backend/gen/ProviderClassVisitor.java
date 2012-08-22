package com.ModDamage.Backend.gen;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.RemappingSignatureAdapter;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.util.CheckClassAdapter;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ReflectionMagic;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SettableDataProvider;

public class ProviderClassVisitor extends ClassVisitor
{
	final String newIName;
	final String pattern;
	
	final Type owner;
	final Method getMethod;
	final Type provides;
	final Type startType;
	
	public ProviderClassVisitor(ClassVisitor cv, final String newIName, Type owner, Method getMethod, final Type provides, final Type startType, String pattern)
	{
		super(Opcodes.ASM4, new RemappingClassAdapter(cv, new Remapper()
			{
				@Override
				public String map(String typeName)
				{
					if (typeName.endsWith("Provider$template"))
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
		
		this.newIName = newIName;
		this.pattern = pattern;
		
		this.owner = owner;
		this.getMethod = getMethod;
		this.provides = provides;
		this.startType = startType;
	}
	
	@Override
	public MethodVisitor visitMethod(final int access, final String name, final String desc, String signature, String[] exceptions)
	{
		MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("get"))
		{
			return new MethodVisitor(api, mv) {
					GeneratorAdapter mg = new GeneratorAdapter(mv, access, name, desc);
					
					@Override public void visitInsn(int opcode) {
						if (opcode == ARETURN) { super.visitInsn(opcode); return; }
						
						mv.visitVarInsn(ALOAD, 1);
						mg.invokeStatic(owner, getMethod);
						
						//mv.visitInsn(ARETURN);
					}
					
					@Override public void visitMaxs(int maxStack, int maxLocals) {
						super.visitMaxs(4, maxLocals);
					}
				};
		}
		else if (name.equals("provides"))
		{
			return new MethodVisitor(api, mv) {
					@Override public void visitInsn(int opcode) {
						if (opcode == ARETURN) { super.visitInsn(opcode); return; }
						
						mv.visitLdcInsn(provides);
						
						//mv.visitInsn(ARETURN);
					}
				};
		}
		else if (name.equals("toString"))
		{
			return new MethodVisitor(api, mv) {
					//GeneratorAdapter mg = new GeneratorAdapter(mv, access, name, desc);
					
					@Override public void visitInsn(int opcode) {
						if (opcode != ARETURN) return;
						
						mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
						mv.visitInsn(DUP);
						mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V");
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, newIName, "startDP", "Lcom/ModDamage/EventInfo/IDataProvider;");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;");
						mv.visitLdcInsn(pattern);
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
						mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
						mv.visitInsn(ARETURN);
					}
					
					@Override public void visitMaxs(int maxStack, int maxLocals) {
						mv.visitMaxs(4, maxLocals);
					}
				};
		}
		
		return mv;
	}
	

	
	public static void createProviderClass(Type owner, Method method, Type provides, Type startsWith, String pattern)
	{
		ClassReader cr;
		ClassLoader cl = ModDamage.getPluginConfiguration().plugin.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream("com/ModDamage/Backend/gen/ProviderClassVisitor"+
				"$Provider$template.class");
		if (is == null) return;
		try
		{
			cr = new ClassReader(is);
		}
		catch (Exception e) {
			System.err.println("PROVIDER "+e); return; }
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
		
		ProviderClassVisitor rcv = new ProviderClassVisitor(new CheckClassAdapter(cw), ReflectionMagic.providerIName, owner, method, provides, startsWith, pattern);
		
		cr.accept(rcv, 0);
		
		ReflectionMagic.registerClass(ReflectionMagic.providerClassName, cw.toByteArray());
	}


	

	public static class Provider$template<Provides, StartsWith> extends DataProvider<Provides, StartsWith>
	{

		public Provider$template(Class<StartsWith> wantStart, IDataProvider<StartsWith> startDP)
		{
			super(wantStart, startDP);
		}

		@Override
		public Provides get(StartsWith start, EventData data) throws BailException
		{
			return null;
		}

		@Override
		public Class<Provides> provides()
		{
			return null;
		}
		
		@Override
		public String toString()
		{
			return null;
			//return startDP + "_suffix";
		}
	}
	
	static class SettableProvider$template<Provides, StartsWith> extends SettableDataProvider<Provides, StartsWith>
	{

		public SettableProvider$template(Class<StartsWith> wantStart, IDataProvider<StartsWith> startDP)
		{
			super(wantStart, startDP);
		}

		@Override
		public Provides get(StartsWith start, EventData data) throws BailException
		{
			return null;
		}

		@Override
		public boolean isSettable()
		{
			return false;
		}

		@Override
		public void set(StartsWith start, EventData data, Provides value) throws BailException
		{
			
		}

		@Override
		public Class<Provides> provides()
		{
			return null;
		}
		
		@Override
		public String toString()
		{
			return null;
			//return startDP + "_suffix";
		}
	}
}