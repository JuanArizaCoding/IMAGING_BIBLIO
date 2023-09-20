package interfaz;

import java.awt.Component;
import java.nio.file.Path;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTreeCellRendererComponent(JTree tree,Object value,boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
        Path path=(Path)value;
        setText(path.getFileName().toString());
        return this;
    }
}
