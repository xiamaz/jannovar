package de.charite.compbio.jannovar.filter;

import de.charite.compbio.jannovar.impl.intervals.IntervalEndExtractor;

/**
 * Extraction of interval ends of {@link Gene} objects.
 */
class GeneIntervalEndExtractor implements IntervalEndExtractor<Gene> {
	public int getBegin(Gene gene) {
		return gene.region.beginPos;
	}

	public int getEnd(Gene gene) {
		return gene.region.endPos;
	}
}