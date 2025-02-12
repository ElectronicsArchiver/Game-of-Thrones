package got.common.entity.animal;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

public class GOTEntityMammoth3DViewer {

	public static String[] ENTITYRENDERER_THIRDPERSONDISTANCE = { "thirdPersonDistance", "field_78490_B" };
	public Minecraft mc = Minecraft.getMinecraft();
	public float defaultThirdPersonDistance;
	public int noticeTicks;
	public boolean ridingDragon;
	public boolean ridingDragonPrev;

	public GOTEntityMammoth3DViewer() {
		defaultThirdPersonDistance = getThirdPersonDistance();
	}

	public float getThirdPersonDistance() {
		return ReflectionHelper.getPrivateValue(EntityRenderer.class, mc.entityRenderer, GOTEntityMammoth3DViewer.ENTITYRENDERER_THIRDPERSONDISTANCE);
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent evt) {
		if (evt.phase != TickEvent.Phase.START || mc.thePlayer == null) {
			return;
		}
		ridingDragon = mc.thePlayer.ridingEntity instanceof GOTEntityMammoth;

		if (ridingDragon && !ridingDragonPrev) {
			setThirdPersonDistance(9);
			noticeTicks = 70;
		} else if (!ridingDragon && ridingDragonPrev) {
			setThirdPersonDistance(defaultThirdPersonDistance);
			noticeTicks = 0;
		} else if (noticeTicks > 0) {
			noticeTicks--;
		}

		ridingDragonPrev = ridingDragon;
	}

	public void setThirdPersonDistance(float thirdPersonDistance) {
		ReflectionHelper.setPrivateValue(EntityRenderer.class, mc.entityRenderer, thirdPersonDistance, GOTEntityMammoth3DViewer.ENTITYRENDERER_THIRDPERSONDISTANCE);
	}
}