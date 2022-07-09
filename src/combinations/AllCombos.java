package combinations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.TraverseEvent;

public class AllCombos {
	
	private static boolean doCombos;
	private static ArrayList<String> selections = new ArrayList<String>();
	private static ArrayList<String> combos = new ArrayList<String>();
	
	private static Text txtTest;
	
	
	
	public static void makeCombos() {
		
		for(int i = 1; i <= selections.size(); i++) {
			
			// combines and puts into list
			combos.add(String.join("", selections));
			
			// go further into the string
			combos = makeCombosHelper((ArrayList<String>)selections.clone(), 1);
			
			// restore original & move first string down
			if(i < selections.size()) {
				Collections.swap(selections, 0, i-1);
				Collections.swap(selections, 0, i);
			}
		}
		
		// sort and remove dupes
		Collections.sort(combos);
		for(int i = 0; i < combos.size(); i++) {
			if(Collections.frequency(combos, combos.get(i)) > 1) {
				for(int j = Collections.frequency(combos, combos.get(i))-1; j > 0; j--) {
					combos.remove(i+j);
				}
			}
		}
	}
	
	
	private static ArrayList<String> makeCombosHelper(ArrayList<String> selection, int pos) {
		
		for(int i = pos+1; i <= selection.size(); i++) {
			
			// go further into the string
			combos = makeCombosHelper((ArrayList<String>)selection.clone(), pos+1);
			
			// makes the combinations
			if(i < selection.size()) {
				Collections.swap(selection, pos, i-1);
				Collections.swap(selection, pos, i);
				combos.add(String.join("", selection));
			}
			
		}
		return combos;
	}

	
	public static void main(String[] args) {
		
		
		Display display = Display.getDefault();
		Shell shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		shell.setImage(SWTResourceManager.getImage(AllCombos.class, "/combinations/thing.ico"));
		shell.setSize(450, 265);
		shell.setText("Combination Maker");
		shell.setLayout(null);
		
		Label lblType = new Label(shell, SWT.NONE);
		lblType.setAlignment(SWT.CENTER);
		lblType.setBounds(117, 10, 199, 39);
		lblType.setFont(SWTResourceManager.getFont("Century Gothic", 24, SWT.NORMAL));
		lblType.setText("Output Type:");
		
		Button btnRadioButton = new Button(shell, SWT.RADIO);
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCombos = true;
			}
		});
		btnRadioButton.setBounds(67, 57, 130, 21);
		btnRadioButton.setFont(SWTResourceManager.getFont("Century Gothic", 12, SWT.NORMAL));
		btnRadioButton.setText("Combinations");
		
		Button btnRadioButton_1 = new Button(shell, SWT.RADIO);
		btnRadioButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCombos = false;
			}
		});
		btnRadioButton_1.setBounds(250, 57, 103, 21);
		btnRadioButton_1.setFont(SWTResourceManager.getFont("Century Gothic", 12, SWT.NORMAL));
		btnRadioButton_1.setText("Anagrams");
		
		Label lblInput = new Label(shell, SWT.NONE);
		lblInput.setAlignment(SWT.CENTER);
		lblInput.setBounds(173, 110, 88, 39);
		lblInput.setFont(SWTResourceManager.getFont("Century Gothic", 24, SWT.NORMAL));
		lblInput.setText("Input:");
		
		txtTest = new Text(shell, SWT.BORDER | SWT.CENTER);
		txtTest.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent arg0) {
				if(arg0.detail == SWT.TRAVERSE_RETURN) {
					selections.add(txtTest.getText());
					txtTest.setText("");
				}
			}
		});
		txtTest.setBounds(144, 160, 145, 21);
		txtTest.setFont(SWTResourceManager.getFont("Century Gothic", 12, SWT.NORMAL));
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(24, 100, 385, 2);
		
		Button btnDone = new Button(shell, SWT.NONE);
		btnDone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter ext = new FileNameExtensionFilter("Microsoft Excel 97-2003 Worksheet (.xls)", ".xls");
				fileChooser.setFileFilter(ext);
				if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					if(!doCombos) {
						String word = new String(selections.get(0));
						selections.clear();
						while(word.length() != 0) {
							selections.add(word.substring(0, 1));
							word = word.substring(1);
						}
					}
					
					makeCombos();
					
					Workbook wb = new XSSFWorkbook();
					Sheet sheet = wb.createSheet("Possible Combinations");
					
					for(int i  = 0; i < combos.size(); i++) {
						Row row = sheet.createRow(i);
						row.createCell(0).setCellValue(combos.get(i));
					}
					
					OutputStream fileOut = null;
					try {
						fileOut = new FileOutputStream(fileChooser.getSelectedFile() + ".xls");
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						wb.write(fileOut);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
			}
		});
		btnDone.setFont(SWTResourceManager.getFont("Century Gothic", 9, SWT.NORMAL));
		btnDone.setBounds(179, 190, 75, 25);
		btnDone.setText("Run");

		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}
}
