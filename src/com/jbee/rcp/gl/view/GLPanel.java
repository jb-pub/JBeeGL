package com.jbee.rcp.gl.view;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.jbee.rcp.gl.engine.Render;
import com.jbee.rcp.gl.object.Window;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.jface.resource.JFaceResources;

public class GLPanel extends Composite {

	/**
	 * 저장할 Image Panel
	 */
	protected Image imgPanel;
	
	/**
	 * 메모리 GC객체
	 */
	protected GC memGC;
	
	/**
	 * GL Panel에 표시할 Window
	 */
	protected Window window;
	
	/**
	 * Rendering을 실행할 객체
	 */
	protected Render rendering;
	
	/**
	 * 현재 눌려있는 마우스
	 *  = -1 일 때 아무것도 안 눌린것
	 */
	protected int clickedMouseButton = -1;
	
	/**
	 * 시야 위치를 구하기 위한 변수 X
	 */
	private double x = 45;

	/**
	 * 시야 위치를 구하기 위한 변수 Y
	 */
	private double y = 30;
	
	private int oldX;
	private int oldY;
	
	/**
	 * 시야를 구하기 위한 변수 원점으로부터의 거리
	 */
	private double length = 500;

	/**
	 * 축을 그릴 것인지
	 */
	private boolean drawAxis;
	
	/**
	 * 프로젝션 모드는
	 */
	private int projectionMode;
	
	/**
	 *  쉐이딩 모드는
	 */
	private int shadingMode;
	
	/**
	 * 컨트롤 키가 눌린 상태
	 */
	private boolean ctrlKeyDowned;
	
	/**
	 * SWT 객체들
	 */
	protected Table objects = null;

	protected Spinner sp_rotX = null;	// rotate X
	protected Spinner sp_rotY = null;	// rotate Y
	protected Spinner sp_rotZ = null;	// rotate Z

	protected Text tt_zoomRatio = null;	// object scale
	protected Slider sc_zoom = null;

	protected Text tt_transX = null;	// transformation X
	protected Text tt_transY = null;	// transformation Y
	protected Text tt_transZ = null;	// transformation Z

	protected Canvas canvas = null; // 3D 데이터가 그려질 패널

	protected Button cb_applyRT = null; // 실시간 변화할 것인지 설정

	protected Button modeParallel = null;	// parallel 프로젝션
	protected Button modePerspective = null; // perspective 프로젝션

	protected Text tt_objID = null;
	
	protected Slider sl_rotX = null;
	protected Slider sl_rotY = null;
	protected Slider sl_rotZ = null;

	protected Button bt_objAdd = null;
	protected Button bt_objDel = null;
	
	protected Label lb_scale = null;

	private Button bt_apply = null;

	private Label label = null;
	private Label label1 = null;
	private Label label2 = null;
	private Label label3 = null;
	private Label label4 = null;
	private Label label5 = null;
	private Label label6 = null;
	private Label label8 = null;
	private Label label9 = null;
	private Label label10 = null;

	private SashForm sashForm = null;
	private SashForm sashForm1 = null;

	private Composite controlPanel = null;
	private Composite objectPanel = null;
	protected Composite objectInfo = null;
	private Composite composite1 = null;

	private Composite composite = null;

	private Button mode_wirefame = null;

	private Button mode_flat = null;

	private Label label11 = null;

	private Label label12 = null;

	private Button mode_gouraud = null;

	private Button mode_phong = null;

	private Button cb_showAxis = null;

	private Button drawingDueMove = null;

	private Button mode_toon = null;

	/**
	 * 생성자
	 * @param parent
	 * @param style
	 */
	public GLPanel(Composite parent, int style) {
		super(parent, style);
		
		drawAxis = true;
		projectionMode = Render.PERSPECTIVE;
		shadingMode = Render.MODE_TOON; 
		
		initialize();
		
		// window & rendering 모듈 초기화
		window = new Window();
		rendering = new Render();

		// 최초 패널 그리기
		getDisplay().timerExec(200, new Runnable(){
			public void run(){
				canvas.redraw();
			}
		});
		
		canvas.addControlListener(new ControlAdapter(){
			public void controlResized(ControlEvent e) {
				getDisplay().timerExec(200, new Runnable(){
					public void run(){
						canvas.redraw();
					}
				});
				canvas.redraw();
			}
		});
	}

	/**
	 * 패널 초기화
	 *
	 */
	private void initialize() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 1;
		gridLayout1.makeColumnsEqualWidth = true;
		setLayout(gridLayout1);
		createSashForm();
		createControlPanel();
		setSize(new Point(600, 513));
	}
	
	/**
	 * This method initializes objectPanel	
	 *
	 */
	private void createObjectPanel() {
		GridData gridData13 = new GridData();
		gridData13.grabExcessHorizontalSpace = true;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.horizontalSpan = 1;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.makeColumnsEqualWidth = false;
		gridLayout2.numColumns = 2;
		objectPanel = new Composite(sashForm, SWT.NONE);
		objectPanel.setLayout(gridLayout2);
		objectPanel.setLayoutData(gridData2);
		createSashForm1();
		cb_applyRT = new Button(objectPanel, SWT.CHECK);
		cb_applyRT.setText("Realtime");
		cb_applyRT.setLayoutData(gridData13);
		cb_applyRT.setSelection(true);
		bt_apply = new Button(objectPanel, SWT.NONE);
		bt_apply.setText("Apply");
		bt_apply.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				reRendering(true);
			}
		});

	}
	

	/**
	 * This method initializes sashForm1	
	 *
	 */
	private void createSashForm1() {
		GridData gridData8 = new GridData();
		gridData8.horizontalSpan = 2;
		gridData8.verticalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.horizontalAlignment = GridData.FILL;
		sashForm1 = new SashForm(objectPanel, SWT.NONE);
		sashForm1.setOrientation(SWT.VERTICAL);
		createComposite1();
		createObjectInfo();
		sashForm1.setLayoutData(gridData8);
		sashForm1.setWeights(new int[]{1,1});
	}

	/**
	 * This method initializes composite1	
	 *
	 */
	private void createComposite1() {
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 3;
		composite1 = new Composite(sashForm1, SWT.NONE);
		composite1.setLayout(gridLayout4);
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.horizontalAlignment = GridData.BEGINNING;
		GridData gridData3 = new GridData();
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.horizontalSpan = 3;
		gridData3.horizontalAlignment = GridData.FILL;
		label = new Label(composite1, SWT.NONE);
		label.setText("Objects");
		label.setLayoutData(gridData4);
		bt_objAdd = new Button(composite1, SWT.NONE);
		bt_objAdd.setText("Add");
		bt_objDel = new Button(composite1, SWT.NONE);
		bt_objDel.setText("Del");
		objects = new Table(composite1, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);
		objects.setHeaderVisible(true);
		objects.setLayoutData(gridData3);
		objects.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(objects, SWT.NONE);
		tableColumn.setWidth(80);
		tableColumn.setText("ID");
		TableColumn tableColumn1 = new TableColumn(objects, SWT.NONE);
		tableColumn1.setWidth(40);
		tableColumn1.setText("v");
		TableColumn tableColumn2 = new TableColumn(objects, SWT.NONE);
		tableColumn2.setWidth(40);
		tableColumn2.setText("f");
	}


	/**
	 * This method initializes objectInfo	
	 *
	 */
	private void createObjectInfo() {
		GridData gridData24 = new GridData();
		gridData24.horizontalAlignment = GridData.FILL;
		gridData24.widthHint = 40;
		gridData24.verticalAlignment = GridData.CENTER;
		GridData gridData23 = new GridData();
		gridData23.horizontalAlignment = GridData.FILL;
		gridData23.widthHint = -1;
		gridData23.grabExcessHorizontalSpace = true;
		gridData23.verticalAlignment = GridData.CENTER;
		GridData gridData22 = new GridData();
		gridData22.verticalAlignment = GridData.CENTER;
		gridData22.widthHint = -1;
		gridData22.grabExcessHorizontalSpace = true;
		gridData22.horizontalAlignment = GridData.FILL;
		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = GridData.FILL;
		gridData21.widthHint = -1;
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.verticalAlignment = GridData.CENTER;
		GridData gridData20 = new GridData();
		gridData20.horizontalAlignment = GridData.END;
		gridData20.verticalAlignment = GridData.CENTER;
		GridData gridData19 = new GridData();
		gridData19.horizontalAlignment = GridData.END;
		gridData19.verticalAlignment = GridData.CENTER;
		GridData gridData18 = new GridData();
		gridData18.horizontalAlignment = GridData.END;
		gridData18.verticalAlignment = GridData.CENTER;
		GridData gridData17 = new GridData();
		gridData17.horizontalSpan = 2;
		GridData gridData16 = new GridData();
		gridData16.horizontalSpan = 2;
		GridData gridData15 = new GridData();
		gridData15.horizontalSpan = 2;
		GridData gridData141 = new GridData();
		gridData141.horizontalAlignment = GridData.BEGINNING;
		gridData141.verticalAlignment = GridData.CENTER;
		GridData gridData131 = new GridData();
		gridData131.horizontalAlignment = GridData.FILL;
		gridData131.heightHint = -1;
		gridData131.grabExcessHorizontalSpace = true;
		gridData131.widthHint = -1;
		gridData131.verticalAlignment = GridData.CENTER;
		GridData gridData91 = new GridData();
		gridData91.horizontalAlignment = GridData.END;
		gridData91.horizontalIndent = 0;
		gridData91.grabExcessHorizontalSpace = false;
		gridData91.verticalAlignment = GridData.CENTER;
		GridData gridData81 = new GridData();
		gridData81.horizontalAlignment = GridData.END;
		gridData81.horizontalIndent = 0;
		gridData81.grabExcessHorizontalSpace = false;
		gridData81.verticalAlignment = GridData.CENTER;
		GridData gridData71 = new GridData();
		gridData71.horizontalAlignment = GridData.END;
		gridData71.horizontalIndent = 0;
		gridData71.grabExcessHorizontalSpace = false;
		gridData71.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.CENTER;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.END;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalSpan = 3;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.horizontalSpan = 2;
		gridData6.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.marginWidth = 0;
		gridLayout3.numColumns = 3;
		gridLayout3.verticalSpacing = 2;
		gridLayout3.horizontalSpacing = 5;
		gridLayout3.marginHeight = 0;
		GridData gridData5 = new GridData();
		gridData5.grabExcessVerticalSpace = true;
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.FILL;
		gridData5.horizontalSpan = 2;
		gridData5.grabExcessHorizontalSpace = true;
		objectInfo = new Composite(sashForm1, SWT.NONE);
		objectInfo.setLayoutData(gridData5);
		objectInfo.setLayout(gridLayout3);
		label1 = new Label(objectInfo, SWT.NONE);
		label1.setText("ID");
		label1.setLayoutData(gridData9);
		tt_objID = new Text(objectInfo, SWT.BORDER | SWT.READ_ONLY);
		tt_objID.setLayoutData(gridData6);
		label2 = new Label(objectInfo, SWT.NONE);
		label2.setText("Rotation ( 0 ~ 360 )");
		label2.setLayoutData(gridData7);
		
		label8 = new Label(objectInfo, SWT.NONE);
		label8.setText("X =");
		label8.setLayoutData(gridData20);
		
		sl_rotX = new Slider(objectInfo, SWT.NONE);
		sl_rotX.setLayoutData(gridData21);
		sl_rotX.setMaximum(370);
		sl_rotX.setMinimum(0);
		sp_rotX = new Spinner(objectInfo, SWT.BORDER);
		sp_rotX.setLayoutData(gridData10);
		sp_rotX.setMaximum(360);
		sp_rotX.setMinimum(0);
		sl_rotX.setData(sp_rotX);
		sp_rotX.setData(sl_rotX);
		
		label9 = new Label(objectInfo, SWT.NONE);
		label9.setText("Y =");
		label9.setLayoutData(gridData19);
		
		sl_rotY = new Slider(objectInfo, SWT.NONE);
		sl_rotY.setLayoutData(gridData22);
		sl_rotY.setMaximum(370);
		sl_rotY.setMinimum(0);
		sp_rotY = new Spinner(objectInfo, SWT.BORDER);
		sp_rotY.setLayoutData(gridData11);
		sp_rotY.setMaximum(360);
		sp_rotY.setMinimum(0);
		sl_rotY.setData(sp_rotY);
		sp_rotY.setData(sl_rotY);
		
		label10 = new Label(objectInfo, SWT.NONE);
		label10.setText("Z =");
		label10.setLayoutData(gridData18);
		
		sl_rotZ = new Slider(objectInfo, SWT.NONE);
		sl_rotZ.setLayoutData(gridData23);
		sl_rotZ.setMaximum(370);
		sl_rotZ.setMinimum(0);
		sp_rotZ = new Spinner(objectInfo, SWT.BORDER);
		sp_rotZ.setLayoutData(gridData12);
		sp_rotZ.setMaximum(360);
		sp_rotZ.setMinimum(0);
		sl_rotZ.setData(sp_rotZ);
		sp_rotZ.setData(sl_rotZ);
		
		label3 = new Label(objectInfo, SWT.NONE);
		label3.setText("Transformation");
		label3.setLayoutData(gridData);
		label4 = new Label(objectInfo, SWT.NONE);
		label4.setText("X = ");
		label4.setLayoutData(gridData91);
		tt_transX = new Text(objectInfo, SWT.BORDER | SWT.READ_ONLY);
		tt_transX.setLayoutData(gridData17);
		label5 = new Label(objectInfo, SWT.NONE);
		label5.setText("Y = ");
		label5.setLayoutData(gridData81);
		tt_transY = new Text(objectInfo, SWT.BORDER | SWT.READ_ONLY);
		tt_transY.setLayoutData(gridData16);
		label6 = new Label(objectInfo, SWT.NONE);
		label6.setText("Z = ");
		label6.setLayoutData(gridData71);
		tt_transZ = new Text(objectInfo, SWT.BORDER | SWT.READ_ONLY);
		tt_transZ.setLayoutData(gridData15);
		lb_scale = new Label(objectInfo, SWT.NONE);
		lb_scale.setText("Scale");
		lb_scale.setLayoutData(gridData141);
		sc_zoom = new Slider(objectInfo, SWT.NONE);
		sc_zoom.setLayoutData(gridData131);
		sc_zoom.setMinimum(1);
		sc_zoom.setSelection(100);
		sc_zoom.setMaximum(200);
		tt_zoomRatio = new Text(objectInfo, SWT.BORDER | SWT.READ_ONLY);
		tt_zoomRatio.setLayoutData(gridData24);
	}

	/**
	 * This method initializes sashForm	
	 *
	 */
	private void createSashForm() {
		GridData gridData14 = new GridData();
		gridData14.verticalAlignment = GridData.FILL;
		gridData14.grabExcessHorizontalSpace = true;
		gridData14.grabExcessVerticalSpace = true;
		gridData14.horizontalAlignment = GridData.FILL;
		sashForm = new SashForm(this, SWT.NONE);
		sashForm.setLayoutData(gridData14);
		createCanvas();
		createObjectPanel();
		sashForm.setWeights(new int[]{3,1});
	}
	
	/**
	 * This method initializes controlPanel	
	 *
	 */
	private void createControlPanel() {
		GridData gridData26 = new GridData();
		gridData26.horizontalAlignment = GridData.FILL;
		gridData26.grabExcessHorizontalSpace = true;
		gridData26.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.verticalAlignment = GridData.FILL;
		controlPanel = new Composite(this, SWT.NONE);
		controlPanel.setLayoutData(gridData1);
		controlPanel.setLayout(gridLayout);
		label12 = new Label(controlPanel, SWT.NONE);
		label12.setText("Projection Mode");
		label12.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.BANNER_FONT));
		modeParallel = new Button(controlPanel, SWT.RADIO);
		modeParallel.setText("Parallel");
		modeParallel.setSelection(projectionMode==Render.PARALLEL);
		modeParallel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				projectionMode = Render.PARALLEL;
				reRendering(true);
			}
		});
		modePerspective = new Button(controlPanel, SWT.RADIO);
		modePerspective.setText("Perspective");
		modePerspective.setLayoutData(gridData26);
		modePerspective.setSelection(projectionMode==Render.PERSPECTIVE);
		cb_showAxis = new Button(controlPanel, SWT.CHECK);
		cb_showAxis.setText("Show X,Y,Z Axis");
		cb_showAxis.setSelection(drawAxis);
		cb_showAxis.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				if(drawAxis != cb_showAxis.getSelection()){
					drawAxis= cb_showAxis.getSelection();
					reRendering(true);
				}
			}
		});
		createComposite();
		drawingDueMove = new Button(controlPanel, SWT.CHECK);
		drawingDueMove.setText("Wireframe on Move");
		drawingDueMove.setSelection(true);
		modePerspective.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				projectionMode = Render.PERSPECTIVE;
				reRendering(true);
			}
		});
	}
	
	/**
	 * This method initializes composite	
	 *
	 */
	private void createComposite() {
		GridData gridData25 = new GridData();
		gridData25.horizontalSpan = 3;
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.numColumns = 6;
		gridLayout5.marginHeight = 0;
		gridLayout5.marginWidth = 0;
		composite = new Composite(controlPanel, SWT.NONE);
		composite.setLayout(gridLayout5);
		composite.setLayoutData(gridData25);
		label11 = new Label(composite, SWT.NONE);
		label11.setText("Shading Mode");
		label11.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.BANNER_FONT));
		mode_wirefame = new Button(composite, SWT.RADIO);
		mode_wirefame.setText("Wireframe");
		mode_wirefame.setSelection(shadingMode==Render.MODE_WIREFRAME);
		mode_wirefame.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				shadingMode = Render.MODE_WIREFRAME;
				reRendering(true);
			}
		});
		mode_flat = new Button(composite, SWT.RADIO);
		mode_flat.setText("Flat");
		mode_flat.setSelection(shadingMode==Render.MODE_FLAT);
		mode_flat.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				shadingMode = Render.MODE_FLAT;
				reRendering(true);
			}
		});
		mode_gouraud = new Button(composite, SWT.RADIO);
		mode_gouraud.setText("Gouraud");
		mode_gouraud.setSelection(shadingMode==Render.MODE_GOURAUD);
		mode_gouraud.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				shadingMode = Render.MODE_GOURAUD;
				reRendering(true);
			}
		});
		mode_phong = new Button(composite, SWT.RADIO);
		mode_phong.setText("Phong");
		mode_phong.setSelection(shadingMode==Render.MODE_PHONG);
		mode_phong.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				shadingMode = Render.MODE_PHONG;
				reRendering(true);
			}
		});
		mode_toon = new Button(composite, SWT.RADIO);
		mode_toon.setText("Toon");
		mode_toon.setSelection(shadingMode==Render.MODE_TOON);
		mode_toon.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				shadingMode = Render.MODE_TOON;
				reRendering(true);
			}
		});
	}



	/**
	 * This method initializes canvas	
	 *
	 */
	private void createCanvas() {
		canvas = new Canvas(sashForm, SWT.NO_BACKGROUND | SWT.BORDER);
		
		/**
		 * PaintListener = canvas에 3D 이미지를 보여주도록 함. 
		 */
		canvas.addPaintListener(new PaintListener(){
			public void paintControl(PaintEvent e){
				// canvas의 크기 구함.
				Rectangle canvasSize = canvas.getClientArea();
				if(canvasSize.width!=0){
					if(imgPanel==null){
						createCanvasImg(canvasSize);						
					}
					else{
						Rectangle imageSize = imgPanel.getBounds();
						// 이미지 사이즈가 켄버스 사이즈와 다르면 Image와 memGC제 생성
						if(!imageSize.equals(canvasSize)){
							createCanvasImg(canvasSize);							
						}	
						int left = (canvasSize.width - imageSize.width) / 2;
						int top = (canvasSize.height - imageSize.height) / 2;
						e.gc.drawImage(imgPanel, 0, 0, imageSize.width, imageSize.height, left, top, imageSize.width, imageSize.height);
					}
				}
				else{
					e.gc.fillRectangle(canvasSize);
				}
			}
		});
		
		// 마우스 버튼 리스너
		canvas.addMouseListener(new MouseAdapter(){
			int preMode = 0;
			public void mouseDown(MouseEvent e){
				oldX = e.x;
				oldY = e.y;
				clickedMouseButton = e.button;
				if(drawingDueMove.getSelection()){
					preMode = shadingMode;
					shadingMode = Render.MODE_WIREFRAME;
				}
			}
			public void mouseUp(MouseEvent e){
				clickedMouseButton = -1;
				if(drawingDueMove.getSelection()){
					shadingMode = preMode;
					reRendering(true);
				}
			}
		});
		
		// 마우스 Move 리스너 - 시야이동, Object이동에 사용
		canvas.addMouseMoveListener(new MouseMoveListener(){
			public void mouseMove(MouseEvent event){
				if(rendering!=null){
					// 오른쪽 마우스버튼 클릭시는 시점 이동
					if(clickedMouseButton==3){
						moveEyePoint(oldX-event.x,oldY-event.y);
					}
					// 그 외의 버튼이나 버튼이 눌리지 않았을 때는 함수 호출
					else{
						if(ctrlKeyDowned)
							moveMouse(clickedMouseButton, 0, 0, oldX-event.x);
						else
							moveMouse(clickedMouseButton,oldX-event.x,oldY-event.y, 0);
					}
				}
				oldX = event.x;
				oldY = event.y;
			}
		});
		
		// keyboard Listener
		canvas.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==SWT.CONTROL){
					ctrlKeyDowned = true;
				}
			}
			public void keyReleased(KeyEvent e) {
				if(e.keyCode==SWT.CONTROL){
					ctrlKeyDowned = false;
				}
			}
		});
		
		// 시야 거리를 조정하기 위한 Wheel 리스너
		canvas.addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(Event e){
				if(clickedMouseButton==1) moveMouse(clickedMouseButton,0,0, e.count/3*10);
				else{
					length += (e.count/3)*30;
					moveEyePoint(0,0);
					reRendering(true);
				}
			}
		});

	}

	/**
	 * 새로운 imgPanel 생성 (윈도우 크기 변경시)
	 * @param size
	 */
	public void createCanvasImg(Rectangle size){
		imgPanel = new Image(getDisplay(),size);
		memGC = new GC(imgPanel);
		try {
			int drawing_mode = shadingMode | projectionMode;
			if(drawAxis) drawing_mode |= Render.DRAW_AXIS;
			window.setZBufferSize(size.width,size.height);
			rendering.rendering(memGC, window, size.width, size.height, drawing_mode);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 시야 변환
	 * @param xChanged
	 * @param yChanged
	 */
	private void moveEyePoint(int xChanged, int yChanged){
		x -= ((double)xChanged)/3.0;
		y -= ((double)yChanged)/3.0;
		
		if(x<0) x+=360;
		else if(x>=360) x-=360;
		if(y<-90) y=-90;
		else if(y>90) y=90;
		
		double xyLength = length * Math.cos(Math.PI*y/180);
		double eye_y = length * Math.sin(Math.PI*y/180);
		double eye_x = xyLength * Math.cos(Math.PI*x/180);
		double eye_z = xyLength * Math.sin(Math.PI*x/180);
				
		rendering.setEyePoint(eye_x,eye_y,eye_z);
		reRendering(true);
	}
	
	/**
	 * 다시 렌더링하기
	 *
	 */
	protected boolean reRendering(boolean forced){
		if(forced || cb_applyRT.getSelection()){
			try {
				int drawing_mode = shadingMode | projectionMode;
				if(drawAxis) drawing_mode |= Render.DRAW_AXIS;
				Rectangle rect = imgPanel.getBounds();
				rendering.rendering(memGC, window, rect.width, rect.height, drawing_mode);						
			} 
			catch (Exception e) {
				e.printStackTrace();
			}		
			canvas.redraw();
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * 마우스가 움직 일 때 호출됨 (오른쪽 버튼 눌린경우 제외) 
	 * @param button
	 * @param changeX
	 * @param changeY
	 */
	protected void moveMouse(int button, int changeX, int changeY, int changeZ){
		System.out.println(button + " : " + changeX + " , " + changeY + " , " + changeZ);
	}


}  //  @jve:decl-index=0:visual-constraint="10,10"
