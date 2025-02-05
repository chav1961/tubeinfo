package chav1961.tubeinfo;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;

import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.CommandLineParametersException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.useful.JStateString;
import chav1961.tubeinfo.references.interfaces.TubesGroup;
import chav1961.tubeinfo.references.tubes.ElectronicTubesScreen;
import chav1961.tubeinfo.utils.InternalUtils;

public class Application extends JFrame implements LocalizerOwner, LoggerFacadeOwner, AutoCloseable, LocaleChangeListener {
	private static final long 	serialVersionUID = -2254433813417255787L;
	
	public static final String	ARG_DEBUG = "d";
	public static final String	ARG_SOURCE = "src";
	public static final String	APPLICATION_TITLE = "chav1961.tubesReference.application.title";	
	public static final String	MESSAGE_READY = "chav1961.tubesReference.message.ready";	
	
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final JTabbedPane				tabs = new JTabbedPane();
	private final JStateString				state;
	
	public Application(final CountDownLatch latch, final ContentMetadataInterface mdi, final File contentDir) {
		if (latch == null) {
			throw new NullPointerException("Latch can't be null");
		}
		else if (mdi == null) {
			throw new NullPointerException("Metadata interface can't be null");
		}
		else {
			this.mdi = mdi;
			this.localizer = Localizer.Factory.newInstance(mdi.getRoot().getLocalizerAssociated());
			
			PureLibSettings.PURELIB_LOCALIZER.push(localizer);
			PureLibSettings.PURELIB_LOCALIZER.addLocaleChangeListener(this);
			
			this.state = new JStateString(localizer);
			for(TubesGroup item : TubesGroup.values()) {
				final LocaleResource	anno = InternalUtils.getLocaleResource(item);
				
				tabs.addTab("", new ImageIcon(TubesGroup.class.getResource(anno.icon())), createTab(item, contentDir));
			}
			
			state.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			getContentPane().add(tabs, BorderLayout.CENTER);
			getContentPane().add(state, BorderLayout.SOUTH);
			setSize(1024, 768);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					if (canClose()) {
						latch.countDown();
					}
				}
			});
			fillLocalizedStrings();
		}
	}
	
	@Override
	public Localizer getLocalizer() {
		return localizer;
	}

	@Override
	public LoggerFacade getLogger() {
		return state;
	}

	@Override
	public void close() throws RuntimeException {
		PureLibSettings.PURELIB_LOCALIZER.pop();
		dispose();
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		fillLocalizedStrings();
	}

	private JComponent createTab(final TubesGroup group, final File contentDir) {
		switch (group) {
			case GROUP_ELECTRON	:
				try {
					return new ElectronicTubesScreen(localizer, contentDir);
				} catch (IOException e) {
					e.printStackTrace();
					return new JLabel(group.name());
				}
			case GROUP_IMAGES	:
				return new JLabel(group.name());
			case GROUP_ION		:
				return new JLabel(group.name());
			default :
				throw new UnsupportedOperationException("Tube group ["+group+"] is not supported yet");
		}
	}
	
	
	private boolean canClose() {
		return true;
	}
	
	
	private void fillLocalizedStrings() {
		setTitle(getLocalizer().getValue(APPLICATION_TITLE));
		for(TubesGroup item : TubesGroup.values()) {
			final LocaleResource	anno = InternalUtils.getLocaleResource(item);
			
			tabs.setTitleAt(item.ordinal(), getLocalizer().getValue(anno.value()));
			tabs.setToolTipTextAt(item.ordinal(), getLocalizer().getValue(anno.tooltip()));
		}
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final ArgParser			parser = new ApplicationArgParser();
		
		try {
			final ArgParser		parsed = parser.parse(args);
			
			try(final InputStream				is = Application.class.getResourceAsStream("application.xml")) {
				final ContentMetadataInterface	xda = ContentModelFactory.forXmlDescription(is);
				final CountDownLatch			latch = new CountDownLatch(1);

				try(final Application			app = new Application(latch, xda, parsed.getValue(ARG_SOURCE, File.class))) {
				
					app.setVisible(true);
					app.getLogger().message(Severity.note, MESSAGE_READY);
					latch.await();
				}
			}
		} catch (CommandLineParametersException exc) {
			System.err.println(exc.getLocalizedMessage());
			System.err.println(parser.getUsage("tubeinfo"));
			System.exit(128);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.exit(129);
		}
	}

	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new BooleanArg(ARG_DEBUG, false, "turn on debugging trace", false),
			new FileArg(ARG_SOURCE, false, "Directory with tube descriptors", "./tubes")
		};
		
		ApplicationArgParser() {
			super(KEYS);
		}
	}

}
