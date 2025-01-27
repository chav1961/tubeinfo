package chav1961.tubeinfo.references.interfaces;

import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;

@LocaleResourceLocation("i18n:xml:root://chav1961.tubeinfo.references.interfaces.TubeParameter/chav1961/tubeinfo/i18n/i18n.xml")
public enum TubeParameter {
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.myu",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.myu.tt")
	MYU("μ", 100, TubeParameterUnit.NONE),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.s",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.s.tt")
	S("S", 100, TubeParameterUnit.MA_V),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.temperature",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.temperature.tt")
	MAX_TEMPERATURE("t<sub>max</sub>", 100, TubeParameterUnit.CELSIUS),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.input.capacity",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.input.capacity.tt")
	INPUT_CAPACITY("C<sub>in</sub>", 100, TubeParameterUnit.PICOFARADA),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.output.capacity",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.output.capacity.tt")
	OUTPUT_CAPACITY("C<sub>out</sub>", 100, TubeParameterUnit.PICOFARADA),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.transfer.capacity",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.transfer.capacity.tt")
	TRANSFER_CAPACITY("C<sub>tr</sub>", 100, TubeParameterUnit.PICOFARADA),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.cathode.filament.capacity",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.cathode.filament.capacity.tt")
	CATHODE_FILAMENT_CAPACITY("C<sub>cf</sub>", 100, TubeParameterUnit.PICOFARADA),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.anode.anode.capacity",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.anode.anode.capacity.tt")
	ANODE_ANODE_CAPACITY("C<sub>aa</sub>", 100, TubeParameterUnit.PICOFARADA),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.cutoff.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.cutoff.voltage.tt")
	CUTOFF_VOLTAGE("V<sub>cut</sub>", 100, TubeParameterUnit.VOLT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.catchode.filament.leak.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.catchode.filament.leak.current.tt")
	CATHODE_FILAMENT_LEAK_CURRENT("I<sub>cf</sub>", 100, TubeParameterUnit.MICROAMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.grid.leak.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.grid.leak.current.tt")
	GRID_LEAK_CURRENT("I<sub>g</sub>", 100, TubeParameterUnit.MICROAMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.noise.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.noise.voltage.tt")
	NOISE_VOLTAGE("U<sub>noise</sub>", 100, TubeParameterUnit.MILLIVOLT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.noise.resistance",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.noise.resistance.tt")
	NOISE_RESISTANCE("R<sub>noise</sub>", 100, TubeParameterUnit.KOHM),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.power.dissipation",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.power.dissipation.tt")
	POWER_DISSIPATION("P<sub>max</sub>", 100, TubeParameterUnit.WATT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.min.grid1.resistance",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.min.grid1.resistance.tt")
	MIN_GRID_1_RESISTANCE("R<sub>g.min</sub>", 100, TubeParameterUnit.KOHM),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.grid1.resistance",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.grid1.resistance.tt")
	MAX_GRID_1_RESISTANCE("R<sub>g.max</sub>", 100, TubeParameterUnit.KOHM),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.filament.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.filament.current.tt")
	FILAMENT_CURRENT("I<sub>f</sub>", 100, TubeParameterUnit.AMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.filament.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.filament.voltage.tt")
	FILAMENT_VOLTAGE("U<sub>f</sub>", 100, TubeParameterUnit.VOLT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.min.filament.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.min.filament.voltage.tt")
	MIN_FILAMENT_VOLTAGE("U<sub>f.min</sub>", 100, TubeParameterUnit.VOLT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.filament.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.filament.voltage.tt")
	MAX_FILAMENT_VOLTAGE("U<sub>f.max</sub>", 100, TubeParameterUnit.VOLT),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.cathode.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.cathode.current.tt")
	MAX_CATHODE_CURRENT("I<sub>c.max</sub>", 100, TubeParameterUnit.MILLIAMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.pulse.cathode.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.pulse.cathode.current.tt")
	MAX_PULSE_CATHODE_CURRENT("U<sub>c.pulse.max</sub>", 100, TubeParameterUnit.AMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.anode.current",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.anode.current.tt")
	ANODE_CURRENT("I<sub>a</sub>", 100, TubeParameterUnit.MILLIAMPER),
	@LocaleResource(value="chav1961.tubeinfo.references.tubes.tubeParameter.max.anode.voltage",tooltip="chav1961.tubeinfo.references.tubes.tubeParameter.max.anode.voltage.tt")
	MAX_ANODE_VOLTAGE("U<sub>a.max</sub>", 100, TubeParameterUnit.VOLT);
	
	private final String			abbr; 
	private final int				orderIndex;
	private final TubeParameterUnit	unit;
	
	private TubeParameter(final String abbr, final int orderIndex, final TubeParameterUnit unit) {
		this.abbr = abbr;
		this.orderIndex = orderIndex;
		this.unit = unit;
	}

	public String getAbbr() {
		return abbr;
	}
	
	public int getOrderIndex() {
		return orderIndex;
	}
	
	public TubeParameterUnit getUnit() {
		return unit;
	}
}