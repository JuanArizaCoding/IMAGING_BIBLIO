package interfaz;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import imagenes.DataModel;
import imagenes.FolderStructure;
import imagenes.Image;


public class TableAndTreeGenerator implements TableModel,TreeModel {
		DataModel dm;
		String[] columnNames = new String[] {"name", "width", "height","originalDate","latitude",
				"longitude","ISO","Ruta"};
		List<Image>result;
		List<TableModelListener>listeners=new ArrayList<TableModelListener>();
		public TableAndTreeGenerator() {
			try {
				this.result=new ArrayList<Image>();
				this.dm=new DataModel(FolderStructure.biblioPath);
				this.result.addAll(dm.getResult());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void igualarListaAManager() {
			dm.setDefaultResult();
			this.result=dm.getResult();
		}
		
		@Override
		public int getRowCount() {
			return dm.getSize();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==1||columnIndex==2||columnIndex==6) {
				return Integer.class;
			}else if(columnIndex==7){
				return Path.class;
			}else {
				return String.class;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Image i=this.result.get(rowIndex);
			if(columnIndex==0) {
				return i.getName();
			}else if(columnIndex==1) {
				return i.getWidth();
			}else if(columnIndex==2) {
				return i.getHeight();
			}else if(columnIndex==3) {
				return i.getOriginalDate();
			}else if(columnIndex==4) {
				return (""+i.getLatitude()+i.getLatitudeRef());
			}else if(columnIndex==5) {
				return (""+i.getLongitude()+i.getLongitudeRef());
			}else if(columnIndex==6) {
				return i.getISO();
			}else if(columnIndex==7) {
				return i.getRuta();
			}
			return "";
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Image i=this.result.get(rowIndex);
			if(aValue instanceof String) {
				String value=(String)aValue;
				
				if(columnIndex==0) {
					i.setName(value);
				}else if(columnIndex==3) {
					i.setOriginalDate(value);
				}else if(columnIndex==4) {
					String []valores;
					if(value.contains("N")) {
						valores=value.split("N");
					}else {
						valores=value.split("S");
					}
					i.setLatitude(Double.parseDouble(valores[0]));
					i.setLatitudeRef(valores[1]);
				}else if(columnIndex==5) {
					String []valores;
					if(value.contains("W")) {
						valores=value.split("W");
					}else {
						valores=value.split("E");
					}
					i.setLongitude(Double.parseDouble(valores[0]));
					i.setLongitudeRef(valores[1]);
				}
			}else if(aValue instanceof Integer) {
				Integer value=(Integer)aValue;
				
				if(columnIndex==1) {
					i.setWidth(value);
				}else if(columnIndex==2) {
					i.setHeight(value);
				}
			}else if(aValue instanceof Short) {
				Short value=(Short)aValue;
				i.setISO(value);
			}else if(aValue instanceof Path) {
				Path ruta=(Path)aValue;
				if(columnIndex==7) {
					i.setRuta(ruta);
				}
			}
			//Cambia la tabla
			fireTableRowsChanged();
		}
		
		@Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }
        
		@Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }
        
        protected void fireTableRowsChanged() {
            TableModelEvent e=new TableModelEvent(this);
            for (TableModelListener l:listeners) {
                l.tableChanged(e);
            }
        }

        private List<TreeModelListener> listenerList=new ArrayList<>();
        private Path rootPath=FolderStructure.biblioPath;
        
        @Override
        public Object getRoot() {
            return rootPath;
        }

        @Override
        public Object getChild(Object parent, int index) {
            Path pPath=(Path)parent;
            try (DirectoryStream<Path>ds=Files.newDirectoryStream(pPath)) {
                int i=0;
                for (Path p:ds) {
                    if (i==index) {
                        return p;
                    }
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getChildCount(Object parent) {
            Path parentPath=(Path)parent;
            try (DirectoryStream<Path>ds=Files.newDirectoryStream(parentPath)) {
                int count = 0;
                for (Path p:ds) {
                    count++;
                }
                return count;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public boolean isLeaf(Object node) {
            Path p=(Path)node;
            if (Files.isDirectory(p)) { //Si es directorio no es hoja
                return false;
            } else if (p.toString().endsWith(".jpg")) { //Hoja cualquier File .jpg
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {}

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            Path pPath=(Path)parent;
            Path cPath=(Path)child;
            try (DirectoryStream<Path>ds=Files.newDirectoryStream(pPath)) {
                int index=0;
                for (Path p:ds) {
                    if (p.equals(cPath)) {
                        return index;
                    }
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
            listenerList.add(l);
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
            listenerList.remove(l);
        }
        
        protected void fireTreeDataChanged() {
        	TreeModelEvent e=new TreeModelEvent(this,(TreePath)null);
        	for(TreeModelListener l:listenerList) {
        		l.treeStructureChanged(e);
        	}
        }
}
