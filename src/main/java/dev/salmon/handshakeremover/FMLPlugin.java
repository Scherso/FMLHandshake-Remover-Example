package dev.salmon.handshakeremover;

import dev.salmon.handshakeremover.asm.ClassTransformer;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @see net.minecraftforge.fml.relauncher.IFMLLoadingPlugin for clarification on the interfaced methods.
 */
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.TransformerExclusions({"dev.salmon.handshakeremover.asm"})
public class FMLPlugin implements IFMLLoadingPlugin
{

    /**
     * Check for runtime deobfuscation. If the user is in a developer environment, this will be true.
     */
    public static Boolean IS_OBF = false;

    /**
     * @return Return a list of classes that implements the {@link net.minecraft.launchwrapper.IClassTransformer}
     * interface.
     */
    @NotNull
    @Override
    public final String[] getASMTransformerClass()
    {
        return (new String[] {ClassTransformer.class.getName()});
    }

    @Nullable
    @Override
    public String getModContainerClass()
    {
        return (null);
    }

    @Nullable
    @Override
    public String getSetupClass()
    {
        return (null);
    }

    /**
     * Inject core-mod data into this core-mod This data includes: <br> {@link #IS_OBF} - Whether the game is running in
     * a deobfuscated environment.
     */
    @Override
    public void injectData(Map<String, Object> map)
    {
        IS_OBF = (Boolean) map.get("runtimeDeobfuscationEnabled");
    }

    @Nullable
    @Override
    public String getAccessTransformerClass()
    {
        return (null);
    }

}
