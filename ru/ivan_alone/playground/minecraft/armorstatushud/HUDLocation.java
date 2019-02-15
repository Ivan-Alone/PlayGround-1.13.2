package ru.ivan_alone.playground.minecraft.armorstatushud;

import net.minecraft.util.NonNullList;

public enum HUDLocation {
	LEFT, RIGHT, LEFT_HORIZONTAL, RIGHT_HORIZONTAL;
	
	public static <T> NonNullList<T> invert(NonNullList<T> list) {
		NonNullList<T> new_list = NonNullList.withSize(list.size(), list.get(0));
		int i = list.size()-1;
		for (T t : list) {
			new_list.set(i, t);
			i--;
		}
		return new_list;
	}
}
