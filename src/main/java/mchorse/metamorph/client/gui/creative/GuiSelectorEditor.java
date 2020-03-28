package mchorse.metamorph.client.gui.creative;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.resizers.ColumnResizer;
import mchorse.mclib.utils.Direction;
import mchorse.mclib.utils.Timer;
import mchorse.metamorph.ClientProxy;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.render.EntitySelector;
import mchorse.metamorph.client.EntityModelHandler;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

public class GuiSelectorEditor extends GuiElement
{
	public GuiListElement<EntitySelector> selectors;

	public GuiElement form;
	public GuiTextElement name;
	public GuiTextElement type;
	public GuiToggleElement active;
	public GuiButtonElement pick;

	private EntitySelector selector;
	private Timer timer = new Timer(200);
	private boolean selecting;

	public GuiSelectorEditor(Minecraft mc)
	{
		super(mc);

		this.selectors = new GuiSelectorListElement(mc, this::fillData);
		this.selectors.sorting().background(0xff000000).setList(EntityModelHandler.selectors);
		this.selectors.context(() ->
		{
			GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc)
				.action(Icons.ADD, "Add a selector", this::addSelector);

			if (!this.selectors.getCurrent().isEmpty())
			{
				menu.action(Icons.REMOVE, "Remove a selector", this::removeSelector);
			}

			return menu;
		});

		this.form = new GuiElement(mc);
		this.name = new GuiTextElement(mc, (name) ->
		{
			this.selector.name = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.name.flex().h(20);
		this.type = new GuiTextElement(mc, (name) ->
		{
			this.selector.type = name;
			this.selector.updateTime();
			this.timer.mark();
		});
		this.type.flex().h(20);
		this.active = new GuiToggleElement(mc, "Enabled", (toggle) ->
		{
			this.selector.enabled = toggle.isToggled();
			this.selector.updateTime();
			this.timer.mark();
		});
		this.active.flex().h(20);
		this.pick = new GuiButtonElement(mc, "Pick morph", (button) ->
		{
			this.selecting = true;
			button.setEnabled(false);
		});
		this.pick.flex().h(20);

		this.selectors.flex().parent(this.area).set(0, 0, 0, 0).anchor(1, 0).x(0.5F, 0).w(0.5F, 0).h(1, 0);
		this.form.flex().parent(this.area).set(0, 0, 0, 0).x(0.5F, 0).w(0.5F, 0).h(1, 0);
		this.form.resizer(new ColumnResizer(this.form, 5).stretch().dontCollect().padding(10));

		GuiLabel title = new GuiLabel(mc, "Entity Selectors");

		title.tooltip("With this feature, you can add morphs to entities by specific name or their type...", Direction.BOTTOM).flex().h(this.font.FONT_HEIGHT);

		GuiLabel name = new GuiLabel(mc, "Name").anchor(0, 1);

		name.flex().h(16);

		GuiLabel type = new GuiLabel(mc, "Type").anchor(0, 1);

		type.flex().h(16);

		this.form.add(title, name, this.name, type, this.type, this.active,this.pick);

		this.markContainer().add(this.form, this.selectors);

		this.selectors.setIndex(0);
		this.fillData(this.selectors.getCurrent());
	}

	private void addSelector()
	{
		EntityModelHandler.selectors.add(new EntitySelector());
		this.selectors.update();
		this.timer.mark();
	}

	private void removeSelector()
	{
		if (!this.selectors.current.isEmpty())
		{
			EntitySelector selector = this.selectors.getCurrent().get(0);

			selector.name = "";
			selector.type = "";
			selector.morph = null;
			selector.updateTime();

			int current = this.selectors.current.get(0);

			EntityModelHandler.selectors.remove(current);
			this.selectors.setIndex(current - 1);
			this.fillData(this.selectors.getCurrent());
			this.selectors.update();
			this.timer.mark();
		}
	}

	private void fillData(List<EntitySelector> selectors)
	{
		this.selector = null;
		this.selecting = false;
		this.form.setVisible(!selectors.isEmpty());
		this.pick.setEnabled(true);

		if (selectors.isEmpty())
		{
			return;
		}

		EntitySelector selector = selectors.get(0);

		this.selector = selector;
		this.name.setText(selector.name);
		this.type.setText(selector.type);
		this.active.toggled(selector.enabled);
	}

	@Override
	public void draw(GuiContext context)
	{
		if (this.timer.checkReset())
		{
			ClientProxy.models.saveSelectors();
		}

		this.area.draw(0xaa000000);

		super.draw(context);
	}

	public void setMorph(AbstractMorph morph)
	{
		if (this.selecting && this.selector != null)
		{
			this.selector.morph = morph == null ? null : morph.clone(true);
		}

		this.pick.setEnabled(true);
		this.selecting = false;
		this.selector.updateTime();
		this.timer.mark();
	}

	public static class GuiSelectorListElement extends GuiListElement<EntitySelector>
	{
		public GuiSelectorListElement(Minecraft mc, Consumer<List<EntitySelector>> callback)
		{
			super(mc, callback);

			this.scroll.scrollItemSize = 16;
		}

		@Override
		protected String elementToString(EntitySelector element, int i, int x, int y, boolean hover, boolean selected)
		{
			return element.name + " (" + element.type + ") - " + (element.morph == null ? "null" : element.morph.getDisplayName());
		}
	}
}