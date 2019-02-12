
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.linguistic.LanguageModels;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.LabelFilterTestBase;
import org.junit.Test;

/**
 * Test cases for {@link StopLabelFilter}.
 */
public class StopWordLabelFilterEnglishTest extends LabelFilterTestBase
{
    @Override
    protected void initializeFilters(LabelFilterProcessor filterProcessor)
    {
        filterProcessor.completeLabelFilter.enabled = true;
        filterProcessor.stopWordLabelFilter.enabled = true;
    }

    @Test
    public void testEmpty()
    {
        final int [] expectedLabelsFeatureIndex = new int [] {};

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testNonStopWords()
    {
        createDocuments("coal . mining", "coal . mining");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            0, 1
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWords()
    {
        createDocuments("I . HAVE . coal", "I . HAVE . coal");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            wordIndices.get("coal")
        };

        check(expectedLabelsFeatureIndex);
    }

    @Test
    public void testStopWordsInPhrases()
    {
        createDocuments("of coal mining for", "of coal mining for");

        final int [] expectedLabelsFeatureIndex = new int []
        {
            4
        };

        check(expectedLabelsFeatureIndex, 0);
    }

    @Override
    protected ILexicalData createLexicalData() {
        return LanguageModels.english().lexicalData;
    }
}