package view;
import java.awt.Color;
import java.awt.Component;

import javax.swing.*;

public class ImageListCellRenderer implements ListCellRenderer<Object> {

	/**
	 * This method is for the Jlist in the view that stores the buttons.
	 * @param jlist
	 * @param value
	 * @param cellIndex
	 * @param isSelected
	 * @param cellHasFocus
	 * @return component or new JLabel
	 */
	@Override
	public Component getListCellRendererComponent(JList<?> jlist, Object value, int cellIndex, boolean isSelected,
			boolean cellHasFocus) {
		if (value instanceof JPanel) {
			Component component = (Component) value;
			component.setForeground(Color.white);
			component.setBackground(isSelected ? UIManager.getColor("Table.focusCellForeground") : Color.white);
			return component;
		} else {
			return new JLabel("");
		}
	}

} 