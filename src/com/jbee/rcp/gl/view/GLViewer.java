package com.jbee.rcp.gl.view;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TableItem;

import com.jbee.rcp.gl.object.MObject;
import com.jbee.rcp.gl.object.Matrix;
import com.jbee.rcp.gl.object.Model;
import com.jbee.rcp.gl.object.SpotLight;
import com.jbee.rcp.gl.object.Vertex;

public class GLViewer extends GLPanel{

	/**
	 * ���� ���õ� Model
	 */
	private MObject selected;
	
	public GLViewer(Composite parent, int style) {
		super(parent, style);
		selected = null;
		setObjectCtrl();
		setButtonCtrl();
		setObjectsPanel();
	}
	
	/**
	 * Object �߰� ���� ��ư
	 *
	 */
	private void setButtonCtrl(){
		bt_objAdd.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(getShell(), SWT.NONE);
				String path = dlg.open();
				File file = new File(path);
				try{
					Model model = Model.parse(file);
					if(model!=null){
						window.addModel(model);
						objects.select(objects.indexOf(addTableItem(model)));
						selected = model;
						setObjectInfo(model);
						reRendering(true);
					}
				}
				catch(Exception er){
					er.printStackTrace();
					MessageDialog.openError(getShell(), "File Load Exception", "Error to load <"+path+">");
				}
			}
		});
		bt_objDel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				int index = objects.getSelectionIndex();
				if(index>=0){
					TableItem item = objects.getItem(index);
					Model model = (Model) item.getData();
					window.removeModel(model);
					item.dispose();
					reRendering(true);
				}
				if(objects.getItemCount()>0){
					TableItem item = objects.getItem(0);
					Model model = (Model) item.getData();
					selected = model;
					setObjectInfo(model);
				}
				else{
					selected = null;
					setObjectInfo(null);
				}
			}
		});
	}
	
	/**
	 * Object Controller - table ���� ����
	 *
	 */
	private void setObjectCtrl(){
		objects.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem) e.item;
				MObject obj = (MObject) item.getData();
				if(obj.isVisible()!=item.getChecked()){
					System.out.println(obj.getId());
					obj.setVisible(item.getChecked());
					reRendering(true);
				}
				else{
					selected = obj;
					setObjectInfo(selected);
				}
			}
		});
		
		/**
		 * ���ǳ� ������
		 */
		SelectionListener spinnerListener = new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if(e.widget instanceof Spinner){
					Spinner sp = (Spinner) e.widget;
					// 0~360 �������� ��� �ݺ��ǵ���
					int selection = sp.getSelection();
					if(selection == 0) sp.setSelection(360);
					if(selection == 360) sp.setSelection(0);
					// slider�� ����
					Slider sl = (Slider) sp.getData();
					sl.setSelection(selection);
				}
				else{
					Slider sl = (Slider) e.widget;
					// 0~360 �������� ��� �ݺ��ǵ���
					int selection = sl.getSelection();
					if(selection == 0) sl.setSelection(360);
					if(selection == 360) sl.setSelection(0);
					// slider�� ����
					Spinner sp = (Spinner) sl.getData();
					sp.setSelection(selection);
				}
				// model�� ����
				if(selected!=null){
					selected.setRotate(sp_rotX.getSelection(), sp_rotY.getSelection(), sp_rotZ.getSelection());
				}
				// realtime���� �ε��� ������ �ٷ� ȭ�鿡 ������
				reRendering(false);
			}
		};
		
		sp_rotX.addSelectionListener(spinnerListener);
		sp_rotY.addSelectionListener(spinnerListener);
		sp_rotZ.addSelectionListener(spinnerListener);
		sl_rotX.addSelectionListener(spinnerListener);
		sl_rotY.addSelectionListener(spinnerListener);
		sl_rotZ.addSelectionListener(spinnerListener);
		
		sc_zoom.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				// model�� ����
				if(selected!=null){
					int selection = sc_zoom.getSelection();
					double zoom = 1.0;
					if(selection<=100){
						zoom = ((double)selection)/100.0;
					}
					else{
						selection = selection-100;
						zoom = 1.0 + ((double)selection)/10.0;	
					}
					if(selected instanceof SpotLight){
						if(zoom>1){
							zoom = 1;
							sc_zoom.setSelection(100);
						}
					}
					selected.setZoom(zoom);
					tt_zoomRatio.setText(Double.toString(selected.getZoom()));
				}
				// ȭ�鿡 ����
				reRendering(false);
			}			
		});
		
	}

	
	/**
	 * Object���� �г� �ʱ�ȭ
	 *
	 */
	private void setObjectsPanel(){
		// Table�� �⺻ Window�� ���� �߰�
		Model model = null;
		
		int size = 0;
		int count = 0;
		ArrayList<SpotLight> lights = window.getLights();
		if(lights!=null){
			size = lights.size();
			SpotLight light = null;
			for(int i=0;i<size;i++){
				light = lights.get(i);
				addTableItem(light);
				count++;
			}
		}

		size = window.getNofModel();
		for(int i=0;i<size;i++){
			model = window.getModel(i);
			addTableItem(model);
			if(i==0){
				objects.select(count);
				selected = model;
				setObjectInfo(model);
			}		
		}
		

	}
	
	/**
	 * Table�� Item�߰��ϱ�
	 * @param model
	 */
	private TableItem addTableItem(Model model){
		TableItem item = new TableItem(objects, SWT.NONE);
		item.setText(0, model.getId());
		item.setText(1, Integer.toString(model.getNofVertex()));
		item.setText(2, Integer.toString(model.getNofFace()));
		item.setChecked(model.isVisible());
		item.setData(model);
		return item;
	}
	
	/**
	 * Table�� Item�߰��ϱ�
	 * @param light
	 */
	private TableItem addTableItem(SpotLight light){
		TableItem item = new TableItem(objects, SWT.NONE);
		item.setText(0, light.getId());
		item.setChecked(light.isVisible());
		item.setData(light);
		return item;
	}
	
	/**
	 * Table���� �� Model�� ���õǾ� Info�� ������
	 * @param model
	 */
	private void setObjectInfo(MObject model){
		
		tt_objID.setText(model!=null?model.getId():"");
		
		Vertex rotate = model!=null?model.getRotateVertex():new Vertex();
		
		sp_rotX.setSelection((int)rotate.x);
		sp_rotY.setSelection((int)rotate.y);
		sp_rotZ.setSelection((int)rotate.z);
		
		sl_rotX.setSelection((int)rotate.x);
		sl_rotY.setSelection((int)rotate.y);
		sl_rotZ.setSelection((int)rotate.z);
		
		Matrix trans = model!=null?model.getTrans():new Matrix();
		tt_transX.setText(Double.toString(trans.m[3]));
		tt_transY.setText(Double.toString(trans.m[7]));
		tt_transZ.setText(Double.toString(trans.m[11]));
		
		double zoom = model.getZoom();
		tt_zoomRatio.setText(Double.toString(zoom));
		if(zoom<=1.0){
			sc_zoom.setSelection((int) (zoom*100.0));
		}
		else{
			sc_zoom.setSelection(((int)((zoom - 1.0)*10.0)) + 100);
		}
		
		// lightSource�� ���� ȸ��, �����Ϸ� ��ȯ ����
		if(model instanceof SpotLight)
			lb_scale.setText("Bright");
		else
			lb_scale.setText("Scale");
	}
	
	/**
	 * ���콺�� ���� �� �� ȣ��� (������ ��ư ������� ����) 
	 * @param button
	 * @param changeX
	 * @param changeY
	 */
	protected void moveMouse(int button, int changeX, int changeY, int changeZ){
		if(button==1&&selected!=null){
			Matrix trans = selected.getTrans();
			double x = trans.m[3]-changeX;
			double y = trans.m[7]+changeY;
			double z = trans.m[11]+changeZ;
			tt_transX.setText(Double.toString(x));
			tt_transY.setText(Double.toString(y));
			tt_transZ.setText(Double.toString(z));
			selected.setTrans(x, y, z);
			// ȭ�鿡 ����
			reRendering(false);			
		}
	}


}
