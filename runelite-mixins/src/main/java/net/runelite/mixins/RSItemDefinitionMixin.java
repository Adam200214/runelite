package net.runelite.mixins;

import net.runelite.api.events.PostItemDefinition;
import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Inject;
import net.runelite.api.mixins.MethodHook;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Replace;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSItemDefinition;
import net.runelite.rs.api.RSModel;

@Mixin(RSItemDefinition.class)
public abstract class RSItemDefinitionMixin implements RSItemDefinition
{
	private static final int DEFAULT_CUSTOM_SHIFT_CLICK_INDEX = -2;

	@Shadow("client")
	private static RSClient client;

	@Inject
	private int shiftClickActionIndex = DEFAULT_CUSTOM_SHIFT_CLICK_INDEX;

	@Inject
	private int modelOverride = -1;

	@Override
	@Inject
	public void setModelOverride(int id)
	{
		modelOverride = id;
	}

	@Inject
	RSItemDefinitionMixin()
	{
	}

	@Inject
	@Override
	public boolean isStackable()
	{
		return getIsStackable() != 0;
	}

	@Inject
	@Override
	public void setShiftClickActionIndex(int shiftClickActionIndex)
	{
		this.shiftClickActionIndex = shiftClickActionIndex;
	}

	@Copy("getShiftClickIndex")
	@Replace("getShiftClickIndex")
	public int copy$getShiftClickActionIndex()
	{
		return shiftClickActionIndex == DEFAULT_CUSTOM_SHIFT_CLICK_INDEX ? copy$getShiftClickActionIndex() : shiftClickActionIndex;
	}

	@Inject
	@Override
	public void resetShiftClickActionIndex()
	{
		shiftClickActionIndex = DEFAULT_CUSTOM_SHIFT_CLICK_INDEX;
	}

	@Inject
	@MethodHook(value = "post", end = true)
	public void post()
	{
		final PostItemDefinition event = new PostItemDefinition();
		event.setItemDefinition(this);
		client.getCallbacks().post(PostItemDefinition.class, event);
	}

	@Copy("getModel")
	@Replace("getModel")
	public RSModel copy$getModel(int quantity)
	{
		if (modelOverride == -1)
		{
			return copy$getModel(quantity);
		}

		return client.getRSItemDefinition(modelOverride).getModel(quantity);
	}

	@Inject
	@Override
	public int getHaPrice()
	{
		int price = getPrice();
		return (int) ((float) price * 0.6f);
	}
}
