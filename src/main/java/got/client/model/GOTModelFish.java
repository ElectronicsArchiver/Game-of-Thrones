package got.client.model;

import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class GOTModelFish extends ModelBase {
	public ModelRenderer body = new ModelRenderer(this, 0, 0);
	public ModelRenderer finTop;
	public ModelRenderer finRight;
	public ModelRenderer finLeft;
	public ModelRenderer finBack;

	public GOTModelFish() {
		body.setRotationPoint(0.0f, 22.0f, -1.0f);
		body.addBox(-0.5f, -2.0f, -3.0f, 1, 3, 6);
		finTop = new ModelRenderer(this, 14, 0);
		finTop.setRotationPoint(0.0f, 0.0f, -1.5f);
		finTop.addBox(0.0f, -2.0f, 0.0f, 0, 2, 4);
		body.addChild(finTop);
		finRight = new ModelRenderer(this, 22, 0);
		finRight.setRotationPoint(0.0f, 0.0f, -1.0f);
		finRight.addBox(-0.5f, -1.0f, 0.0f, 0, 2, 3);
		body.addChild(finRight);
		finLeft = new ModelRenderer(this, 22, 0);
		finLeft.setRotationPoint(0.0f, 0.0f, -1.0f);
		finLeft.addBox(0.5f, -1.0f, 0.0f, 0, 2, 3);
		body.addChild(finLeft);
		finBack = new ModelRenderer(this, 0, 9);
		finBack.setRotationPoint(0.0f, -0.5f, 1.5f);
		finBack.addBox(0.0f, -5.0f, 0.0f, 0, 5, 5);
		body.addChild(finBack);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.render(f5);
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		finTop.rotateAngleX = (float) Math.toRadians(27.0);
		finRight.rotateAngleX = (float) Math.toRadians(-15.0);
		finRight.rotateAngleY = (float) Math.toRadians(-30.0);
		finRight.rotateAngleY += MathHelper.cos(f2 * 0.5f + 3.1415927f) * (float) Math.toRadians(10.0);
		finLeft.rotateAngleX = finRight.rotateAngleX;
		finLeft.rotateAngleY = -finRight.rotateAngleY;
		finBack.rotateAngleX = (float) Math.toRadians(-45.0);
	}
}
