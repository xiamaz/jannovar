package de.charite.compbio.jannovar.annotation;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.AnnotationLocation.RankType;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.ProjectionException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import de.charite.compbio.jannovar.reference.TranscriptPosition;
import de.charite.compbio.jannovar.reference.TranscriptProjectionDecorator;

/**
 * Class for collecting the data for a VCF annotation string.
 *
 * Simplifies building of <code>Object</code> arrays that can then be joined using {@link Joiner}.
 */
class VCFAnnotationData {

	/** predicted effects */
	public ImmutableSortedSet<VariantEffect> effects = ImmutableSortedSet.<VariantEffect> of();
	/** predicted impact */
	public PutativeImpact impact = null;
	/** symbol of affected gene */
	public String geneSymbol = null;
	/** ID of affected gene */
	public String geneID = null;
	/** type of the feature (<code>null</code> or <code>"transcript"</code>). */
	public String featureType = null;
	/** ID of the feature/transcript */
	public String featureID = null;
	/** bio type of the feature, one of "Coding" and "Noncoding". */
	public String featureBioType = null;
	/** exon/intron rank */
	public int rank = -1;
	/** total number of exons/introns */
	public int totalRank = -1;
	/** nucleotide HGVS description */
	public String ntHGVSDescription = null;
	/** amino acid HGVS description */
	public String aaHGVSDescription = null;
	/** transcript position, zero based */
	public int txPos = -1;
	/** transcript length */
	public int txLength = -1;
	/** CDS position */
	public int cdsPos = -1;
	/** CDS length */
	public int cdsLength = -1;
	/** distance */
	public int distance = -1;
	/** additional messages for the annotation */
	public ImmutableSortedSet<AnnotationMessage> messages = ImmutableSortedSet.<AnnotationMessage> of();

	public void setAnnoLoc(AnnotationLocation annoLoc) {
		if (annoLoc == null)
			return;
		if (annoLoc.getRankType() != RankType.UNDEFINED) {
			this.rank = annoLoc.getRank();
			this.totalRank = annoLoc.getTotalRank();
		}

		final TranscriptModel transcript = annoLoc.getTranscript();
		final TranscriptProjectionDecorator projector = new TranscriptProjectionDecorator(transcript);
		final TranscriptPosition txPos;
		if (annoLoc != null && annoLoc.getTXLocation().length() == 0)
			txPos = annoLoc.getTXLocation().getTranscriptBeginPos().shifted(-1); // change length == 0, insertion
		else
			txPos = annoLoc.getTXLocation().getTranscriptBeginPos(); // all other variants
		this.txPos = txPos.getPos();
		this.txLength = annoLoc.getTranscript().txRegion.length();

		try {
			this.cdsPos = projector.projectGenomeToCDSPosition(projector.transcriptToGenomePos(txPos)).getPos();
			this.cdsLength = transcript.cdsTranscriptLength();
		} catch (ProjectionException e) {
			// e.printStackTrace();
			throw new Error("Bug: problem with projection!", e);
		}
	}

	public void setTranscriptAndChange(TranscriptModel tm, GenomeChange change) {
		if (tm == null)
			return;
		featureType = "transcript";
		featureID = tm.accession;
		geneSymbol = tm.geneSymbol;
		geneID = tm.geneID;
		featureBioType = tm.isCoding() ? "Coding" : "Noncoding";

		if (effects.contains(VariantEffect.INTERGENIC_VARIANT) || effects.contains(VariantEffect.UPSTREAM_GENE_VARIANT)
				|| effects.contains(VariantEffect.DOWNSTREAM_GENE_VARIANT)) {
			if (change.getGenomeInterval().isLeftOf(tm.txRegion.getGenomeBeginPos()))
				this.distance = tm.txRegion.getGenomeBeginPos().differenceTo(
						change.getGenomeInterval().getGenomeEndPos());
			else
				this.distance = change.getGenomeInterval().getGenomeBeginPos()
				.differenceTo(tm.txRegion.getGenomeEndPos());
		}
	}

	/**
	 * @param allele
	 *            String with the allele value to prepend to the returned array
	 * @return array of objects to be converted to string and joined, the alternative allele is given by
	 */
	public Object[] toArray(String allele) {
		final Joiner joiner = Joiner.on('&').useForNull("");
		final String effectsString = joiner.join(FluentIterable.from(effects).transform(VariantEffect.TO_SO_TERM));
		return new Object[] { allele, effectsString, impact, geneSymbol, geneID, featureType, featureID,
				featureBioType, getRankString(), ntHGVSDescription, aaHGVSDescription, getTXPosString(),
				getCDSPosString(), getAminoAcidPosString(), getDistanceString(), joiner.join(messages) };
	}

	public String toUnescapedString(String allele) {
		return Joiner.on('|').useForNull("").join(toArray(allele));
	}

	private String escape(String str) {
		String result = CharMatcher.anyOf(",;").or(CharMatcher.WHITESPACE).replaceFrom(str, "_");
		result = CharMatcher.is('=').replaceFrom(result, "%3D");
		result = CharMatcher.is('(').replaceFrom(result, "%28");
		result = CharMatcher.is(')').replaceFrom(result, "%29");
		return result;
	}

	/**
	 * @param allele
	 *            alternative allele value to prepend
	 * @return String for putting into the "ANN" field of the VCF file
	 */
	public String toString(String allele) {
		return escape(toUnescapedString(allele));
	}

	private String getRankString() {
		if (rank == -1)
			return null;
		return Joiner.on('/').join(rank + 1, totalRank);
	}

	private String getTXPosString() {
		if (txPos == -1)
			return null;
		return Joiner.on('/').join(txPos + 1, txLength);
	}

	private String getCDSPosString() {
		if (cdsPos == -1)
			return null;
		if (!featureBioType.equals("Coding"))
			return null;
		return Joiner.on('/').join(cdsPos + 1, cdsLength);
	}

	private String getAminoAcidPosString() {
		if (cdsPos == -1)
			return null;
		if (!featureBioType.equals("Coding"))
			return null;
		return Joiner.on('/').join(cdsPos / 3 + 1, cdsLength / 3);
	}

	private String getDistanceString() {
		if (distance == -1)
			return null;
		return Integer.toString(distance);
	}

}