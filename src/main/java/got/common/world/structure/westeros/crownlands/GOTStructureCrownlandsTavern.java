package got.common.world.structure.westeros.crownlands;

import got.common.world.structure.other.GOTStructureBase;
import got.common.world.structure.westeros.common.GOTStructureWesterosTavern;

public class GOTStructureCrownlandsTavern extends GOTStructureWesterosTavern {
	public GOTStructureCrownlandsTavern(boolean flag) {
		super(flag);
		isCrownlands = true;
	}

	public GOTStructureBase setIsKingsLanding() {
		isKingsLanding = true;
		return this;
	}
}