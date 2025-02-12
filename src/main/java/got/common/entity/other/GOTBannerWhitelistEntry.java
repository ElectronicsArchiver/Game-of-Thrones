package got.common.entity.other;

import java.util.*;

import com.mojang.authlib.GameProfile;

import got.common.GOTBannerProtection;

public class GOTBannerWhitelistEntry {
	public GameProfile profile;
	public Set<GOTBannerProtection.Permission> perms = new HashSet<>();

	public GOTBannerWhitelistEntry(GameProfile p) {
		profile = p;
		if (profile == null) {
			throw new IllegalArgumentException("Banner whitelist entry cannot have a null profile!");
		}
	}

	public void addPermission(GOTBannerProtection.Permission p) {
		perms.add(p);
	}

	public boolean allowsPermission(GOTBannerProtection.Permission p) {
		return isPermissionEnabled(GOTBannerProtection.Permission.FULL) || isPermissionEnabled(p);
	}

	public void clearPermissions() {
		perms.clear();
	}

	public void decodePermBitFlags(int i) {
		setPermissions(GOTBannerWhitelistEntry.static_decodePermBitFlags(i));
	}

	public int encodePermBitFlags() {
		return GOTBannerWhitelistEntry.static_encodePermBitFlags(perms);
	}

	public boolean isPermissionEnabled(GOTBannerProtection.Permission p) {
		return perms.contains(p);
	}

	public Set<GOTBannerProtection.Permission> listPermissions() {
		return perms;
	}

	public void removePermission(GOTBannerProtection.Permission p) {
		perms.remove(p);
	}

	public GOTBannerWhitelistEntry setFullPerms() {
		clearPermissions();
		addPermission(GOTBannerProtection.Permission.FULL);
		return this;
	}

	public void setPermissions(List<GOTBannerProtection.Permission> perms) {
		clearPermissions();
		for (GOTBannerProtection.Permission p : perms) {
			addPermission(p);
		}
	}

	public static List<GOTBannerProtection.Permission> static_decodePermBitFlags(int i) {
		ArrayList<GOTBannerProtection.Permission> decoded = new ArrayList<>();
		for (GOTBannerProtection.Permission p : GOTBannerProtection.Permission.values()) {
			if ((i & p.bitFlag) == 0) {
				continue;
			}
			decoded.add(p);
		}
		return decoded;
	}

	public static int static_encodePermBitFlags(Collection<GOTBannerProtection.Permission> permList) {
		int i = 0;
		for (GOTBannerProtection.Permission p : permList) {
			i |= p.bitFlag;
		}
		return i;
	}
}
