package eyeLog.form;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.EventQueue;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class FormSujeto extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public JPanel getContentPane() {
		return contentPane;
	}

	public JFormattedTextField getEdadTextField() {
		return edadTextField;
	}

	public JTextField getOtrosTextField() {
		return otrosTextField;
	}

	public Choice getGeneroChoice() {
		return generoChoice;
	}

	public Choice getVidaEstiloChoice() {
		return vidaEstiloChoice;
	}

	public TextField getOcupacionTextField() {
		return ocupacionTextField;
	}

	public JFormattedTextField getHorasDuermetextField() {
		return horasDuermetextField;
	}

	public Choice getLentesChoice() {
		return lentesChoice;
	}

	public TextField getDatosLentes() {
		return datosLentes;
	}

	private JFormattedTextField edadTextField;
	private JTextField otrosTextField;
	private Choice generoChoice;
	private Choice vidaEstiloChoice;
	private TextField ocupacionTextField;
	private JFormattedTextField horasDuermetextField;
	private Choice lentesChoice;
	private TextField datosLentes;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FormSujeto frame = new FormSujeto();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FormSujeto() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 342);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel contenidoPanel = new JPanel();
		contentPane.add(contenidoPanel, BorderLayout.CENTER);
		contenidoPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("max(78dlu;default)"),
						FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), },
				new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblEdad = new JLabel("Edad");
		contenidoPanel.add(lblEdad, "2, 2, 2, 1, left, default");

		NumberFormat edadFormat = NumberFormat.getInstance();
		NumberFormatter edadFormatter = new NumberFormatter(edadFormat);
		edadFormatter.setValueClass(Integer.class);
		edadFormatter.setMinimum(1);
		edadFormatter.setMaximum(100);
		edadFormatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		edadFormatter.setCommitsOnValidEdit(true);
		edadTextField = new JFormattedTextField(edadFormatter);
		contenidoPanel.add(edadTextField, "4, 2, left, default");
		edadTextField.setColumns(2);

		JLabel lblGenero = new JLabel("Genero");
		contenidoPanel.add(lblGenero, "2, 4");

		generoChoice = new Choice();
		generoChoice.addItem("Femenino");
		generoChoice.addItem("Masculino");
		contenidoPanel.add(generoChoice, "4, 4, left, default");

		Label lblEstiloVida = new Label("Estilo de vida");
		contenidoPanel.add(lblEstiloVida, "2, 6");

		vidaEstiloChoice = new Choice();
		vidaEstiloChoice.addItem("Sedentaria");
		vidaEstiloChoice.addItem("Activa");
		vidaEstiloChoice.addItem("Muy activa");
		contenidoPanel.add(vidaEstiloChoice, "4, 6, left, default");

		Label lblOcupación = new Label("Ocupaci\u00F3n");
		contenidoPanel.add(lblOcupación, "2, 8");

		ocupacionTextField = new TextField();
		ocupacionTextField.setColumns(10);
		contenidoPanel.add(ocupacionTextField, "4, 8, left, default");

		Label lblHorasSueño = new Label("Cantidad de horas que duerme");
		contenidoPanel.add(lblHorasSueño, "2, 10");

		NumberFormat suenoFormat = NumberFormat.getInstance();
		NumberFormatter suenoFormatter = new NumberFormatter(suenoFormat);
		suenoFormatter.setValueClass(Integer.class);
		suenoFormatter.setMinimum(1);
		suenoFormatter.setMaximum(24);
		suenoFormatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		suenoFormatter.setCommitsOnValidEdit(true);

		horasDuermetextField = new JFormattedTextField(suenoFormatter);
		horasDuermetextField.setColumns(2);
		contenidoPanel.add(horasDuermetextField, "4, 10, left, default");

		Label lblAnteojos = new Label("Usa anteojos?");
		contenidoPanel.add(lblAnteojos, "2, 12");

		lentesChoice = new Choice();
		lentesChoice.addItem("Si");
		lentesChoice.addItem("No");
		contenidoPanel.add(lentesChoice, "4, 12, left, default");

		Label lblDatosLentes = new Label("Datos de los lentes");
		contenidoPanel.add(lblDatosLentes, "2, 14");

		datosLentes = new TextField();
		contenidoPanel.add(datosLentes, "4, 14");

		Label lblOtrosProblemas = new Label("Otros problemas de vista");
		contenidoPanel.add(lblOtrosProblemas, "2, 16");

		otrosTextField = new JTextField();
		contenidoPanel.add(otrosTextField, "4, 16, fill, default");
		otrosTextField.setColumns(10);

		JPanel tituloPanel = new JPanel();
		contentPane.add(tituloPanel, BorderLayout.NORTH);

		JLabel lblCuestionario = new JLabel("Cuestionario");
		tituloPanel.add(lblCuestionario);

		JPanel southPanel = new JPanel();
		contentPane.add(southPanel, BorderLayout.SOUTH);

		JButton btnGuardar = new JButton("Guardar");
		southPanel.add(btnGuardar);
		btnGuardar.addActionListener(new ALFormGuardar(this));
	}

}

class ALFormGuardar implements ActionListener {
	private FormSujeto fs;

	public ALFormGuardar(FormSujeto fs) {
		this.fs = fs;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		File fout = new File("data.txt");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fout);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		try {
			bw.write("edad,genero,estilo de vida,ocupacion,horas descanso,usa lentes,datos lentes,otro");
			bw.newLine();
			bw.write(this.fs.getEdadTextField().getText()+","+this.fs.getGeneroChoice().getSelectedItem()+","+this.fs.getVidaEstiloChoice().getSelectedItem()+","+this.fs.getOcupacionTextField().getText()+","+this.fs.getHorasDuermetextField().getText()+","+this.fs.getLentesChoice().getSelectedItem()+","+this.fs.getDatosLentes().getText()+","+this.fs.getOtrosTextField().getText());
			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.fs.dispose();
	}
}