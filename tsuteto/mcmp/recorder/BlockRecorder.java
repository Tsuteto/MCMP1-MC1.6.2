package tsuteto.mcmp.recorder;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tsuteto.mcmp.core.McmpMain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRecorder extends BlockContainer
{
    private final boolean isActive;
    private Random machineRand;
    private Icon iconSide;

    /**
     * This flag is used to prevent the furnace inventory to be dropped upon
     * block removal, is used internally when the furnace block changes from
     * idle to active and vice-versa.
     */
    private static boolean keepInventory = false;

    public BlockRecorder(int par1, boolean isActive)
    {
        super(par1, Material.iron);
        this.isActive = isActive;
        machineRand = new Random();
    }

    @Override
    public TileEntity createNewTileEntity(World var1)
    {
        return new TileEntityRecorder();
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return McmpMain.blockRecorderIdle.blockID;
    }

    /**
     * Returns the block texture based on the side being looked at. Args: side
     */
    @Override
    public Icon getBlockTexture(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        int metadata = iblockaccess.getBlockMetadata(x, y, z);
        int direction = metadata & 3;
        boolean isDubbing = (metadata & 4) == 4;

        if ((direction == 0 || direction == 2) && side == direction + 3
                || (direction == 1 || direction == 3) && side == direction + 1)
        {
            return this.blockIcon;
        }
        else
        {
            return iconSide;
        }
    }

    /**
     * Returns the block texture based on the side being looked at. Args: side
     */
    @Override
    public Icon getIcon(int par1, int par2)
    {
        if (par1 == 3)
        {
            return this.blockIcon;
        }
        else
        {
            return iconSide;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f, float g, float t)
    {
        TileEntity tile = world.getBlockTileEntity(x, y, z);

        if (tile != null)
        {
            player.openGui(McmpMain.instance, 0, world, x, y, z);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack)
    {
        int i = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4F) / 360F) + 0.5D) & 3;

        if (i == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
        }

        if (i == 1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
        }

        if (i == 2)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 2);
        }

        if (i == 3)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
        }

        if (par6ItemStack.hasDisplayName())
        {
            ((TileEntityRecorder)par1World.getBlockTileEntity(par2, par3, par4)).setCustomName(par6ItemStack.getDisplayName());
        }
    }

    /**
     * Update which block ID the furnace is using depending on whether or not it is burning
     */
    public static void updateFurnaceBlockState(boolean isActive, World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        TileEntity var6 = par1World.getBlockTileEntity(par2, par3, par4);
        keepInventory = true;

        if (isActive)
        {
            par1World.setBlock(par2, par3, par4, McmpMain.blockRecorderActive.blockID);
        }
        else
        {
            par1World.setBlock(par2, par3, par4, McmpMain.blockRecorderIdle.blockID);
        }

        keepInventory = false;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var5, 2);

        if (var6 != null)
        {
            var6.validate();
            par1World.setBlockTileEntity(par2, par3, par4, var6);
        }
    }

    /**
     * Called whenever the block is removed.
     */
    @Override
    public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6)
    {
        if (!keepInventory)
        {
            TileEntityRecorder tileentity = (TileEntityRecorder) world.getBlockTileEntity(par2, par3, par4);

            if (tileentity != null)
            {
                label0:

                    for (int i = 0; i < tileentity.getSizeInventory(); i++)
                    {
                        ItemStack itemstack = tileentity.getStackInSlot(i);

                        if (itemstack == null)
                        {
                            continue;
                        }

                        float f = machineRand.nextFloat() * 0.8F + 0.1F;
                        float f1 = machineRand.nextFloat() * 0.8F + 0.1F;
                        float f2 = machineRand.nextFloat() * 0.8F + 0.1F;

                        do
                        {
                            if (itemstack.stackSize <= 0)
                            {
                                continue label0;
                            }

                            int j = machineRand.nextInt(21) + 10;

                            if (j > itemstack.stackSize)
                            {
                                j = itemstack.stackSize;
                            }

                            itemstack.stackSize -= j;
                            EntityItem entityitem = new EntityItem(world, par2 + f, par3 + f1, par4
                                    + f2, new ItemStack(itemstack.itemID, j, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (float) machineRand.nextGaussian() * f3;
                            entityitem.motionY = (float) machineRand.nextGaussian() * f3 + 0.2F;
                            entityitem.motionZ = (float) machineRand.nextGaussian() * f3;
                            world.spawnEntityInWorld(entityitem);
                        } while (true);
                    }
            }
        }

        super.breakBlock(world, par2, par3, par4, par5, par6);
    }

    @Override
    public void addCreativeItems(ArrayList itemList)
    {
        itemList.add(new ItemStack(this));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.isActive ? "mcmp1:dubbingMachine_front_active" : "mcmp1:dubbingMachine_front_idle");
        this.iconSide = par1IconRegister.registerIcon("mcmp1:dubbingMachine_side");
    }
}
