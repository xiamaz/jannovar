package de.charite.compbio.jannovar.hgvs.change.protein;

import de.charite.compbio.jannovar.hgvs.change.AminoAcidCode;

public class ProteinDuplication extends ProteinChange {

	/** range of one or more amino acids that are deleted */
	private final ProteinRange range;
	/** specification of the deleted characters, can be null */
	private final ProteinSeqDescription seqSpec;

	public static ProteinDuplication build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos) {
		return new ProteinDuplication(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription());
	}

	public static ProteinDuplication build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, String seq) {
		return new ProteinDuplication(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(seq));
	}

	public static ProteinDuplication build(boolean onlyPredicted, String firstAA, int firstPos, String lastAA,
			int lastPos, int len) {
		return new ProteinDuplication(onlyPredicted, ProteinRange.build(firstAA, firstPos, lastAA, lastPos),
				new ProteinSeqDescription(len));
	}

	/**
	 * @param onlyPredicted
	 * @param range
	 * @param seqSpec
	 */
	public ProteinDuplication(boolean onlyPredicted, ProteinRange range, ProteinSeqDescription seqSpec) {
		super(onlyPredicted);
		this.range = range;
		this.seqSpec = seqSpec;
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfPredicted(range.toHGVSString(code) + "dup" + seqSpec.toHGVSString(code));
	}

	@Override
	public String toString() {
		return "ProteinDuplication [range=" + range + ", seqSpec=" + seqSpec + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		result = prime * result + ((seqSpec == null) ? 0 : seqSpec.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProteinDuplication other = (ProteinDuplication) obj;
		if (range == null) {
			if (other.range != null)
				return false;
		} else if (!range.equals(other.range))
			return false;
		if (seqSpec == null) {
			if (other.seqSpec != null)
				return false;
		} else if (!seqSpec.equals(other.seqSpec))
			return false;
		return true;
	}

}
