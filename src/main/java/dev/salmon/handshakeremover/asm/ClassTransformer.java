package dev.salmon.handshakeremover.asm;

import dev.salmon.handshakeremover.FMLHandshakeRemover;
import dev.salmon.handshakeremover.FMLPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Removes your mod-id from {@link FMLHandshakeMessage} when joining severs. <br>
 * The benefit being, blacklisted mod-id's can effectively be bypassed, while
 * keeping the same mod-id.
 *
 * @author Schero (<a href="https://github.com/Scherso">...</a>)
 * @see FMLHandshakeMessage.ModList
 */
public class ClassTransformer implements IClassTransformer
{

	/**
	 * <pre>
	 * EQUIVALENT OF:
	 *
	 * {@code
	 * if (!Minecraft.getMinecraft().isSingleplayer()) {
	 *     this.modTags.keySet().remove("handshakeremover");
	 * }
	 * }
	 *
	 * FOR REFERENCE:
	 *
	 * {@link org.objectweb.asm.Opcodes#GETFIELD} Get an instance field defined by the
	 * owner, name,and descriptor.
	 * {@link org.objectweb.asm.Opcodes#INVOKEVIRTUAL} Invoke a method by defined by the
	 * owner, the method name, and descriptor.
	 * {@link org.objectweb.asm.Opcodes#POP} 'Pop' or disregard the initial value in the
	 * stack.
	 * {@link org.objectweb.asm.Opcodes#IFNE} If the value isn't equal, jump to the label.
	 * </pre>
	 *
	 * @return bytecode instructions from {@link ClassWriter}.
	 */
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (!transformedName.equals("net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage$ModList"))
			return (bytes);

		final ClassReader cr = new ClassReader(bytes);
		final ClassNode   cn = new ClassNode();
		cr.accept(cn, 0);

		for (MethodNode method : cn.methods)
		{
			if (method.name.equals("<init>") && method.desc.equals("(Ljava/util/List;)V"))
			{
				for (final AbstractInsnNode INSN : method.instructions.toArray())
				{
					if (INSN instanceof InsnNode && INSN.getOpcode() == POP)
					{
						LabelNode label = new LabelNode();
						InsnList  list  = new InsnList();
						/* See Java-doc for clarification on the following byte-code. */
						list.add(new MethodInsnNode(INVOKESTATIC, "net/minecraft/client/Minecraft", FMLPlugin.IS_OBF ? "func_71410_x" : "getMinecraft", "()Lnet/minecraft/client/Minecraft;", false));
						list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/client/Minecraft", FMLPlugin.IS_OBF ? "func_71356_B" : "isSingleplayer", "()Z", false));
						list.add(new JumpInsnNode(IFNE, label));
						list.add(new VarInsnNode(ALOAD, 0));
						list.add(new FieldInsnNode(GETFIELD, "net/minecraftforge/fml/common/network/handshake/FMLHandshakeMessage$ModList", "modTags", "Ljava/util/Map;"));
						list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "keySet", "()Ljava/util/Set;", true));
						list.add(new LdcInsnNode(FMLHandshakeRemover.ID)); /* Insert your mod-id here. */
						list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Set", "remove", "(Ljava/lang/Object;)Z", true));
						list.add(new InsnNode(POP));
						list.add(label);
						method.instructions.insertBefore(INSN, list);
					}
				}
			}
		}

		ClassWriter cw = new ClassWriter(cr, 0);
		cn.accept(cw);
		return (cw.toByteArray());
	}

}
