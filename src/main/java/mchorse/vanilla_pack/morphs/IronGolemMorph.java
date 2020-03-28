package mchorse.vanilla_pack.morphs;

import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.capabilities.morphing.IMorphing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Iron golem morph
 * 
 * This morph is responsible for making IronGolem morph great again! This morph 
 * is very powerful. I should make him much slower.
 */
public class IronGolemMorph extends EntityMorph
{
    @Override
    public void update(EntityLivingBase target)
    {
        if (target.motionY > 0)
        {
            target.motionY *= 0.9;
        }
        else
        {
            target.motionX *= 0.5;
            target.motionZ *= 0.5;

            target.motionY *= 1.3;
        }

        target.motionX *= 0.5;
        target.motionZ *= 0.5;

        super.update(target);
    }

    @Override
    public void attack(Entity target, EntityLivingBase source)
    {
        if (this.entity != null)
        {
            this.entity.attackEntityAsMob(target);
        }

        super.attack(target, source);
    }

    @Override
    public AbstractMorph create(boolean isRemote)
    {
        return new IronGolemMorph();
    }
}