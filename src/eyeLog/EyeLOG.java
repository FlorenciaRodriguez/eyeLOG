package eyeLog;

import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvClearMemStorage;
import static org.bytedeco.javacpp.opencv_core.cvGetSeqElem;
import static org.bytedeco.javacpp.opencv_core.cvLoad;
import static org.bytedeco.javacpp.opencv_core.cvPoint;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvCvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvLine;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_ROUGH_SEARCH;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_FIND_BIGGEST_OBJECT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core.CvMemStorage;
import org.bytedeco.javacpp.opencv_core.CvRect;
import org.bytedeco.javacpp.opencv_core.CvScalar;
import org.bytedeco.javacpp.opencv_core.CvSeq;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;

import util.email.SendEmail;
import util.zip.ZipUtils;

/**
 * Clase Principal.
 **/
public class EyeLOG implements Runnable {
	private static String user;

	/**
	 * encabezadoArchivo: el header del archivo separado por comas
	 * <li>
	 * <ul>
	 * Fecha es el timestamp de captura
	 * </ul>
	 * <ul>
	 * Cara = 1 si detectó la cara y cero en caso contrario
	 * </ul>
	 * <ul>
	 * Ojo = 1 si lo detecta
	 * </ul>
	 * <ul>
	 * P(O1) punto central del rectangulo donde detecta el ojo izquierdo
	 * </ul>
	 * <ul>
	 * P(O2) punto central del rectangulo donde detecta el ojo derecho
	 * </ul>
	 * </li>
	 */
	private final static String ENCABEZADO_ARCHIVO_LOG_CSV = "fecha,cara,x(o1),y(o1),x(o2),y(o2),ojo";

	/**
	 * Folder donde guardo todo
	 */
	private final static String FOLDER = "eyeLog";
	/**
	 * Path donde se guardan las imagenes capturadas
	 */
	private final static String FOLDER_IMAGE = FOLDER + "\\img";
	/**
	 * Path del log
	 **/
	private final static String ARCHIVO_LOG = FOLDER + "\\log.csv";

	/**
	 * Método main
	 * 
	 * @param args[0]
	 *            cantidad de segundos tras los que se prende la camara.
	 * @param args[1]
	 *            tiempo durante el que se ejecuta la aplicación.
	 * @param args[2]
	 *            m/h/s
	 * @param args[3]
	 *            user
	 * @param args[4]
	 *            faces
	 * @param args[5]
	 *            eyes
	 * @param args[6]
	 *            eyeD
	 * @param args[7]
	 *            eyeI
	 **/
	public static void main(String[] args) {
		char hms = args[2].toCharArray()[0];
		// Cantidad de horas que ejecuto
		int tiempoEjecucion = Integer.parseInt(args[1]);
		// Dado en segundos - Cada cuánto prendo la camara
		long tiempoRegistroCamara = Long.parseLong(args[0]) * 1000;
		user = args[3];
		String face = args[4];
		String eyes = args[5];
		String eyed = args[6];
		String eyeI = args[7];
		EyeLOG eyeDetect = new EyeLOG(face, eyes, eyed, eyeI);
		eyeDetect.detect(tiempoEjecucion, hms, tiempoRegistroCamara);

	}

	/**
	 * Método principal.
	 * 
	 * @param tiempoRegistroCamara
	 *            cantidad de segundos tras los que se prende la camara.
	 * @param tiempoEjecucion
	 *            tiempo durante el que se ejecuta la aplicación.
	 * @param hms
	 *            m/h/s
	 * 
	 */
	public void detect(int tiempoEjecucion, char hms, long tiempoRegistroCamara) {
		// Para capturar la imagen
		FrameGrabber grabber = null;
		try {
			grabber = FrameGrabber.createDefault(0);
		} catch (Exception e) {
			System.out.println("No se creó el objeto que captura el video");
		}

		// un id para referenciar y guardar la imagen
		int idImg = 0;

		CvMemStorage storage = CvMemStorage.create();

		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();

		Calendar calendar2 = Calendar.getInstance();
		if (hms == 'h')
			calendar2.add(Calendar.HOUR_OF_DAY, tiempoEjecucion);
		if (hms == 'm')
			calendar2.add(Calendar.MINUTE, tiempoEjecucion);
		if (hms == 's')
			calendar2.add(Calendar.SECOND, tiempoEjecucion);

		// Ejecuta según el tiempo especificado
		while ((calendar.compareTo(calendar2) < 0)) {

			IplImage grabbedImage = null;
			try {
				grabber.start();
				OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
				grabbedImage = converter.convert(grabber.grab());
			} catch (Exception e) {
				System.out.println("No arrancó la camara");
			}

			// Datos a guardar en el archivo
			String fecha = now.toString();
			String hayCara = "0";
			String hayOjo = "0";
			String x1 = "0";
			String x2 = "0";
			String y1 = "0";
			String y2 = "0";

			// Guardar la imagen "cruda"
			cvSaveImage(FOLDER_IMAGE + "\\imgA" + idImg + ".jpg", grabbedImage);

			IplImage grayImage = IplImage.create(grabber.getImageWidth(), grabber.getImageHeight(), IPL_DEPTH_8U, 1);
			cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);

			// Detectar cara
			cvClearMemStorage(storage);

			CvSeq face = cvHaarDetectObjects(grayImage, classifierFace, storage, 1.1, 3,
					CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
			for (int i = 0; i < face.total(); i++) {
				@SuppressWarnings("resource")
				CvRect r = new CvRect(cvGetSeqElem(face, i));
				int x = r.x(), y = r.y(), w = r.width(), h = r.height();
				cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x + w, y + h), CvScalar.BLUE, 2, CV_AA, 0);
				hayCara = "1";
			}

			// Detectar ojo izquierdo
			cvClearMemStorage(storage);
			CvSeq eyeL = cvHaarDetectObjects(grayImage, classifierEyeLeft, storage, 1.1, 3,
					CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
			int xL = 0, yL = 0;
			for (int j = 0; j < eyeL.total(); j++) {
				@SuppressWarnings("resource")
				CvRect r1 = new CvRect(cvGetSeqElem(eyeL, j));
				cvRectangle(grabbedImage, cvPoint(r1.x(), r1.y()), cvPoint(r1.x() + r1.width(), r1.y() + r1.height()),
						CvScalar.RED, 2, CV_AA, 0);
				xL = r1.x() + (r1.width() / 2);
				yL = r1.y() + (r1.height() / 2);

			}
			x1 = String.valueOf(xL);
			y1 = String.valueOf(yL);

			// Detectar ojo derecho
			cvClearMemStorage(storage);
			CvSeq eyeR = cvHaarDetectObjects(grayImage, classifierEyeRight, storage, 1.1, 3,
					CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
			int xR = 0, yR = 0;
			for (int j = 0; j < eyeR.total(); j++) {
				@SuppressWarnings("resource")
				CvRect r1 = new CvRect(cvGetSeqElem(eyeR, j));
				cvRectangle(grabbedImage, cvPoint(r1.x(), r1.y()), cvPoint(r1.x() + r1.width(), r1.y() + r1.height()),
						CvScalar.YELLOW, 2, CV_AA, 0);
				xR = r1.x() + (r1.width() / 2);
				yR = r1.y() + (r1.height() / 2);
			}
			x2 = String.valueOf(xR);
			y2 = String.valueOf(yR);

			// Guardar la distancia entre ojos
			cvLine(grabbedImage, cvPoint(xL, yL), cvPoint(xR, yR), CvScalar.GREEN);

			// Detectar ambos ojos
			cvClearMemStorage(storage);
			CvSeq eyes = cvHaarDetectObjects(grayImage, classifierEye, storage, 1.1, 3,
					CV_HAAR_FIND_BIGGEST_OBJECT | CV_HAAR_DO_ROUGH_SEARCH);
			for (int j = 0; j < eyes.total(); j++) {
				@SuppressWarnings("resource")
				CvRect r1 = new CvRect(cvGetSeqElem(eyes, j));
				cvRectangle(grabbedImage, cvPoint(r1.x(), r1.y()), cvPoint(r1.x() + r1.width(), r1.y() + r1.height()),
						CvScalar.MAGENTA, 2, CV_AA, 0);
				hayOjo = "1";
			}

			// Guardar la imagen con las cosas dibujadas
			cvSaveImage(FOLDER_IMAGE + "\\imgB" + idImg + ".jpg", grabbedImage);
			idImg++;
			try {
				bw.write(fecha + "," + hayCara + "," + x1 + "," + y1 + "," + x2 + "," + y2 + "," + hayOjo + "\n");
			} catch (IOException e) {
				System.out.println("No se pudo escrbir en el log");
			}

			try {
				grabber.stop();
			} catch (Exception e1) {
				System.out.println("No se pudo detener el grabber");
			}

			try {
				Thread.sleep(tiempoRegistroCamara);
			} catch (InterruptedException e) {
				System.out.println("No se ejecutó el sleep");
			}
			calendar = Calendar.getInstance();

		}

		try {
			bw.close();
		} catch (IOException e) {
			System.out.println("No se cerró el archivo con el log");
		}
		zipAndSend();
	}

	private BufferedWriter bw;

	public EyeLOG(String face, String ojos, String ojoD, String ojoI) {
		// Creación de carpetas y archivos de salida
		File folder = new File(FOLDER_IMAGE);
		folder.mkdirs();
		String sFichero = ARCHIVO_LOG;
		bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(sFichero));
			bw.write(ENCABEZADO_ARCHIVO_LOG_CSV + "\n");
		} catch (IOException e1) {
			System.out.println("Error al crear el archivo log.csv");
		}

		// Librería
		Loader.load(opencv_objdetect.class);

		classifierFace = new CvHaarClassifierCascade(cvLoad(face));
		classifierEyeLeft = new CvHaarClassifierCascade(cvLoad(ojoI));
		classifierEyeRight = new CvHaarClassifierCascade(cvLoad(ojoD));
		classifierEye = new CvHaarClassifierCascade(cvLoad(ojos));
	}

	/**
	 * Clasificadores
	 */
	private CvHaarClassifierCascade classifierFace;
	private CvHaarClassifierCascade classifierEyeLeft;
	private CvHaarClassifierCascade classifierEyeRight;
	private CvHaarClassifierCascade classifierEye;

	/**
	 * Zip info and send email
	 */
	private void zipAndSend() {
		ZipUtils appZip = new ZipUtils(FOLDER, "EyeLog_" + user);
		appZip.generateFileList(new File(FOLDER));
		appZip.zipIt();
		SendEmail.sendFromGMail("EyeLog_" + user);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
}
