package chav1961.tubeinfo.references.tubes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.svg.SVGPainter;
import chav1961.purelib.ui.swing.useful.svg.SVGParser;
import chav1961.tubeinfo.references.interfaces.PinType;
import chav1961.tubeinfo.references.interfaces.TubeConnector;
import chav1961.tubeinfo.references.interfaces.TubeConnectorType;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup;
import chav1961.tubeinfo.references.interfaces.TubePanelGroup.Pin;

class ConnPicture extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 4745784785481072237L;
	private static final int		POINT_RECT = 10;
	private static final float		POINT_AREA = POINT_RECT * POINT_RECT;
	private static final int		LINE_AREA = 3;
	private static final Cursor		ORDINAL_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	private static final Cursor		START_DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		DRAG_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	private static final Cursor		DROP_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	private static final Cursor		REMOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	
	private final SVGPainter		rightPainter;
	private final Set<PointAndType>	rightPoints = new HashSet<>();
	private final Set<Pin>			leftPoints = new HashSet<>();
	private final List<Line2D>		lines = new ArrayList<>();
	private Point					currentPoint = new Point(0,0);
	private Point					fromPoint = null, toPoint = null;
	private SVGPainter				leftPainter = null;
	
	ConnPicture() throws ContentException {
		this.rightPainter = SVGParser.parse(getClass().getResourceAsStream("tube.svg"));
		this.rightPoints.addAll(Arrays.asList(new PointAndType(TubeConnectorType.ANODE, 75, 5), 
											  new PointAndType(TubeConnectorType.GRID_5, 5, 75), 
											  new PointAndType(TubeConnectorType.GRID_4, 195, 87), 
											  new PointAndType(TubeConnectorType.GRID_3, 5, 100), 
											  new PointAndType(TubeConnectorType.GRID_2, 195, 112), 
											  new PointAndType(TubeConnectorType.GRID_1, 5, 125),
											  new PointAndType(TubeConnectorType.CATHODE, 195, 162),
											  new PointAndType(TubeConnectorType.FILAMENT, 75, 195), 
											  new PointAndType(TubeConnectorType.FILAMENT, 125, 195),
											  new PointAndType(TubeConnectorType.SPECIAL, 195, 50) 
								));
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void setPinout(final TubePanelGroup group) {
		if (group == null) {
			throw new NullPointerException("Tube panel group can't be null");
		}
		else {
			try (final InputStream	is = group.getSvgURL().openStream()) {
				this.leftPainter = SVGParser.parse(is);
				this.leftPoints.clear();
				this.leftPoints.addAll(Arrays.asList(group.getPins()));

				for(int index = lines.size()-1; index >= 0; index--) {
					if (!rightPoints.contains(lines.get(index).getP1()) || !rightPoints.contains(lines.get(index).getP2())) {
						lines.remove(index);
					}
				}
				repaint();
			} catch (ContentException | IOException e) {
				SwingUtils.getNearestLogger(this).message(Severity.warning, e, e.getLocalizedMessage());
			}
		}
	}
	
	List<TubeConnector> getConnectors() {
		final List<TubeConnector>	result = new ArrayList<>();
		
		for(Line2D item : lines) {
			PointAndType	pat = null;
			Pin				pin = null;
			
			for (Pin left : leftPoints) {
				if (item.getP1().distanceSq(left.point) < POINT_AREA || item.getP2().distanceSq(left.point) < POINT_AREA) {
					pin = left;
					break;
				}
			}
			if (pin == null) {
				continue;
			}
			else {
				for (PointAndType right : rightPoints) {
					if (item.getP1().distanceSq(right.point) < POINT_AREA || item.getP2().distanceSq(right.point) < POINT_AREA) {
						pat = right;
						break;
					}
				}
				if (pat == null) {
					continue;
				}
				else {
					result.add(TubeConnector.of(0, pin.pinNumber, PinType.ORDINAL, pat.conn));
				}
			}
		}
		return result;
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		final Graphics2D		g2d = (Graphics2D)g;
		final Color				oldColor = g2d.getColor();
		final AffineTransform	oldAt = g2d.getTransform();
		final AffineTransform	leftAt = new AffineTransform(oldAt);
		final AffineTransform	rightAt = new AffineTransform(oldAt);

		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		rightAt.translate(getWidth()/2, 0);
		rightAt.scale(0.5f*getWidth()/rightPainter.getWidth(), 1.0f*getHeight()/rightPainter.getHeight());
		g2d.setTransform(rightAt);
		rightPainter.paint(g2d);
		if (leftPainter != null) {
			leftAt.translate(0, 0);
			leftAt.scale(0.5f*getWidth()/leftPainter.getWidth(), 1.0f*getHeight()/leftPainter.getHeight());
			g2d.setTransform(leftAt);
			leftPainter.paint(g2d);
		}
		g2d.setTransform(oldAt);
		
		for(PointAndType item : rightPoints) {
			drawPoint(g2d, item.point, nearRight(currentPoint, item.point), getWidth()/2);
		}
		for(Pin item : leftPoints) {
			drawPoint(g2d, item.point, nearLeft(currentPoint, item.point), 0);
		}
		for(Line2D item : lines) {
			drawLine(g2d, item, nearLine(currentPoint, item), 0, getWidth()/2);
		}
		if (fromPoint != null) {
			drawDragLine(g2d, fromPoint, currentPoint);
		}
		g2d.setColor(oldColor);
		g2d.setTransform(oldAt);
	}


	@Override
	public void mouseDragged(final MouseEvent e) {
		if (fromPoint != null && currentPoint != null) {
			drawDragLine(fromPoint, currentPoint, 0, getWidth()/2);
			if (atPoint(currentPoint)) {
				repaint(point2Rect(getPointByPoint(currentPoint)));
			}
			repaint(points2Rect(fromPoint, currentPoint));
			currentPoint = e.getPoint();
			drawDragLine(fromPoint, e.getPoint(), 0, getWidth()/2);
			if (atPoint(e.getPoint())) {
				setCursor(DROP_CURSOR);
				repaint(point2Rect(getPointByPoint(currentPoint)));
			}
			else {
				setCursor(DRAG_CURSOR);
				repaint(points2Rect(fromPoint, currentPoint));
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (atPoint(currentPoint)) {
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		currentPoint = e.getPoint();
		if (atPoint(currentPoint)) {
			setCursor(START_DRAG_CURSOR);
			repaint(point2Rect(getPointByPoint(currentPoint)));
		}
		else if (atLine(currentPoint)) {
			setCursor(REMOVE_CURSOR);
			repaint();
		}
		else {
			if (Objects.equals(getCursor(),ORDINAL_CURSOR)) {
				repaint();
			}
			setCursor(ORDINAL_CURSOR);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (atLine(e.getPoint())) {
			lines.remove(getLineByPoint(e.getPoint()));
			repaint();
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (atPoint(e.getPoint())) {
			fromPoint = getPointByPoint(e.getPoint()); 
			setCursor(DRAG_CURSOR);
			drawDragLine(fromPoint, e.getPoint(), 0, getWidth()/2);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (atPoint(e.getPoint())) {
			toPoint = getPointByPoint(e.getPoint());
			appendLine(fromPoint, toPoint);
			fromPoint = null;
			toPoint = null;
		}
		setCursor(ORDINAL_CURSOR);
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (fromPoint != null) {
			drawDragLine(fromPoint, currentPoint, 0, getWidth()/2);
		}
	}

	private boolean nearRight(final Point current, final Point another) {
		final Point	rightCurrent = new Point(current.x - getWidth()/2, current.y);
		
		return another.distanceSq(rightCurrent) < POINT_AREA;
	}

	private boolean nearLeft(final Point current, final Point another) {
		return another.distanceSq(current) < POINT_AREA;
	}
	
	private boolean atPoint(final Point current) {
		final Point	rightCurrent = new Point(current.x - getWidth()/2, current.y);
		
		for(PointAndType item : rightPoints) {
			if (item.point.distanceSq(rightCurrent) < POINT_AREA) {
				return true;
			}
		}
		for(Pin item : leftPoints) {
			if (item.point.distanceSq(current) < POINT_AREA) {
				return true;
			}
		}
		return false;
	}

	private boolean nearLine(final Point current, final Line2D line) {
		return line.ptLineDist(current) < LINE_AREA;
	}
	
	private boolean atLine(final Point current) {
		for(Line2D item : lines) {
			if (item.ptLineDist(current) < LINE_AREA) {
				return true;
			}
		}
		return false;
	}

	private Point getPointByPoint(final Point current) {
		final Point	rightCurrent = new Point(current.x - getWidth()/2, current.y);
		
		for(PointAndType item : rightPoints) {
			if (item.point.distanceSq(rightCurrent) < POINT_AREA) {
				return new Point(item.point.x + getWidth()/2, item.point.y);
			}
		}
		for(Pin item : leftPoints) {
			if (item.point.distanceSq(current) < POINT_AREA) {
				return item.point;
			}
		}
		return null;
	}

	private Line2D getLineByPoint(final Point current) {
		for(Line2D item : lines) {
			if (item.ptLineDist(current) < LINE_AREA) {
				return item;
			}
		}
		return null;
	}

	private void appendLine(final Point from, final Point to) {
		for(Line2D item : lines) {
			if (item.getP1().equals(from) && item.getP2().equals(to) || item.getP1().equals(to) && item.getP2().equals(from)) {
				return;
			}
		}
		lines.add(new Line2D.Float(from, to));
		SwingUtilities.invokeLater(()->repaint());
	}

	private Rectangle point2Rect(final Point point) {
		return new Rectangle(point.x - POINT_RECT/2, point.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
	}

	private Rectangle points2Rect(final Point first, final Point second) {
		return new Rectangle(Math.min(first.x, second.x) - 1, Math.min(first.y, second.y) - 1, Math.abs(second.x - first.x) + 2, Math.abs(second.y - first.y) + 2);
	}
	
	private void drawPoint(final Graphics2D g2d, final Point item, final boolean highLight, final int deltaX) {
		final Color		oldColor = g2d.getColor();
		
		g2d.setColor(highLight ? Color.RED : Color.GRAY);
		g2d.fillOval(item.x+deltaX-POINT_RECT/2, item.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
		g2d.setColor(Color.WHITE);
		g2d.drawOval(item.x+deltaX-POINT_RECT/2, item.y - POINT_RECT/2, POINT_RECT, POINT_RECT);
		g2d.setColor(oldColor);
	}
	
	private void drawLine(final Graphics2D g2d, final Line2D item, final boolean highLight, final int deltaX1, final int deltaX2) {
		final Color		oldColor = g2d.getColor();
		final Stroke	oldStroke = g2d.getStroke();
		
		g2d.setColor(highLight ? Color.RED : Color.BLUE);
		g2d.setStroke(new BasicStroke(1.0f));
		g2d.draw(item);
		g2d.setColor(oldColor);
		g2d.setStroke(oldStroke);
	}

	private void drawDragLine(final Point from, final Point to, final int deltaX1, final int deltaX2) {
		// TODO Auto-generated method stub
		
	}
	
	private void drawDragLine(final Graphics2D g2d, final Point from, final Point to) {
		final Color		oldColor = g2d.getColor();
		final Stroke	oldStroke = g2d.getStroke();
		
		g2d.setColor(Color.BLUE);
		g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[] {5.0f, 5.0f}, 0));
		g2d.drawLine(from.x, from.y, to.x, to.y);
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}

	static class PointAndType {
		private final TubeConnectorType	conn;
		private final Point	point;
		
		public PointAndType(final TubeConnectorType conn, final int x, final int y) {
			this.conn = conn;
			this.point = new Point(x, y);
		}

		@Override
		public String toString() {
			return "PointAndType [conn=" + conn + ", point=" + point + "]";
		}
	}
}
