package jannovar.reference;

// TODO(holtgrem): Introduce CDSPosition for explicitely expressing this third coordinate system?

/**
 * Position on a transcript.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class TranscriptPosition {
	/** the selected coordinate system (0-based, 1-based) */
	private final PositionType positionType;
	/** the transcript that this position is relative to */
	private final TranscriptModel transcript;
	/** the position within the transcript */
	final int pos;

	/** construct transcript position with one-based coordinate system */
	public TranscriptPosition(TranscriptModel transcript, int pos) {
		this.positionType = PositionType.ONE_BASED;
		this.transcript = transcript;
		this.pos = pos;
	}

	/** construct transcript position with selected coordinate system */
	public TranscriptPosition(TranscriptModel transcript, int pos, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = transcript;
		this.pos = pos;
	}

	/** construct transcript position from other with selected coordinate system */
	public TranscriptPosition(TranscriptPosition other, PositionType positionType) {
		this.positionType = positionType;
		this.transcript = other.transcript;
		int delta = 0;
		if (other.positionType == PositionType.ZERO_BASED && this.positionType == PositionType.ONE_BASED)
			delta += 1;
		else if (other.positionType == PositionType.ONE_BASED && this.positionType == PositionType.ZERO_BASED)
			delta -= 1;
		this.pos = other.pos + delta;
	}

	/** create a copy with the given position type. */
	public TranscriptPosition withPositionType(PositionType positionType) {
		return new TranscriptPosition(this, positionType);
	}

	/**
	 * Return shifted TranscriptPosition.
	 *
	 * @param delta
	 *            the value to add to the position
	 * @return the position shifted by <tt>delta</tt>
	 */
	public TranscriptPosition shifted(int delta) {
		return new TranscriptPosition(transcript, pos + delta, positionType);
	}

	/**
	 * @return the pos
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @return the positionType
	 */
	public PositionType getPositionType() {
		return positionType;
	}

	/**
	 * @return the transcript
	 */
	public TranscriptModel getTranscript() {
		return transcript;
	}

	/*
	 * Returns string with one-based position.
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int pos = this.pos + (positionType == PositionType.ZERO_BASED ? 1 : 0);
		return String.format("%s:%d", this.transcript.getAccessionNumber(), pos);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + pos;
		result = prime * result + ((positionType == null) ? 0 : positionType.hashCode());
		result = prime * result + ((transcript == null) ? 0 : transcript.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TranscriptPosition other = (TranscriptPosition) obj;
		if (pos != other.pos)
			return false;
		if (positionType != other.positionType)
			return false;
		if (transcript == null) {
			if (other.transcript != null)
				return false;
		} else if (!transcript.equals(other.transcript))
			return false;
		return true;
	}

}
