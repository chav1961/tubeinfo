package chav1961.tubeinfo.references.interfaces;

public interface TubeConnector {
	int getLampNo();
	int getPin();
	PinType getPinType();
	TubeConnectorType getType();
	
	public static TubeConnector of(final int lampNo, final int pin, final PinType pinType, final TubeConnectorType connType) {
		if (lampNo < 0 || lampNo > 3) {
			throw new IllegalArgumentException("Lamp number ["+lampNo+"] out of range 0..3");
		}
		else if (pin < 1) {
			throw new IllegalArgumentException("Pin ["+pin+"] must be greater or equals than 1");
		}
		else if (pinType == null) {
			throw new NullPointerException("Pin type can't be null");
		}
		else if (connType == null) {
			throw new NullPointerException("Connection type can't be null");
		}
		else {
			return new TubeConnector() {
				@Override public TubeConnectorType getType() {return connType;}
				@Override public PinType getPinType() {return pinType;}
				@Override public int getPin() {return pin;}
				@Override public int getLampNo() {return lampNo;}
				
				@Override
				public String toString() {
					return "TubeConnector [lampNo=" + lampNo + ", pin=" + pin + ", pinType=" + pinType + ", connType=" + connType + "]";
				}
			};
		}
	}
}
