package com.andremion.heroes.ui;

import com.andremion.heroes.api.MarvelApi;
import com.andremion.heroes.api.MarvelCallback;
import com.andremion.heroes.api.MarvelResult;
import com.andremion.heroes.api.data.CharacterVO;
import com.andremion.heroes.api.json.CharacterData;
import com.andremion.heroes.api.json.CharacterDataContainer;
import com.andremion.heroes.api.json.CharacterDataWrapper;
import com.andremion.heroes.api.json.SectionList;
import com.andremion.heroes.api.json.SectionSummary;
import com.andremion.heroes.api.json.Url;
import com.andremion.heroes.api.util.DataParser;
import com.andremion.heroes.ui.home.MainContract;
import com.andremion.heroes.ui.home.MainPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DataParser.class)
public class MainPresenterTest {

    @Mock
    private MarvelApi mMarvelApi;

    @Mock
    private MainContract.View mView;

    @Captor
    private ArgumentCaptor<MarvelCallback<CharacterDataWrapper>> mListCharactersCallback;

    @InjectMocks
    private MainPresenter mPresenter;

    @Before
    public void setUp() {
        mockStatic(DataParser.class);
        mPresenter.attachView(mView);
    }

    @Test
    public void initScreenTest() {

        final int OFFSET = 0;
        final int TOTAL = MarvelApi.MAX_FETCH_LIMIT;
        final List<CharacterVO> ENTRIES = new ArrayList<>();
        final String ATTRIBUTION = "";
        MarvelResult<CharacterVO> result = new MarvelResult<>();
        result.setOffset(OFFSET);
        result.setTotal(TOTAL);
        result.setEntries(ENTRIES);
        result.setAttribution(ATTRIBUTION);
        when(DataParser.parse(any(CharacterDataWrapper.class))).thenReturn(result);


        final boolean HAS_MORE = true;
        CharacterDataWrapper data = new CharacterDataWrapper();
        data.attributionText = ATTRIBUTION;
        CharacterDataContainer dataContainer = new CharacterDataContainer();
        dataContainer.offset = OFFSET;
        dataContainer.total = TOTAL;
        CharacterData characterData = Mockito.mock(CharacterData.class);
        characterData.urls = new Url[0];
        characterData.comics = new SectionList();
        characterData.comics.items = new SectionSummary[0];
        characterData.series = new SectionList();
        characterData.series.items = new SectionSummary[0];
        characterData.stories = new SectionList();
        characterData.stories.items = new SectionSummary[0];
        characterData.events = new SectionList();
        characterData.events.items = new SectionSummary[0];
        dataContainer.results = new CharacterData[]{characterData};
        data.data = dataContainer;

        mPresenter.initScreen();

        InOrder inOrder = inOrder(mView);
        inOrder.verify(mView).showInfoDialog();
        inOrder.verify(mView).showProgress();

        verify(mMarvelApi).listCharacters(eq(OFFSET), mListCharactersCallback.capture());
        mListCharactersCallback.getValue().onResult(any(CharacterDataWrapper.class));

        inOrder.verify(mView).showResult(ENTRIES);
        inOrder.verify(mView).showAttribution(ATTRIBUTION);
        inOrder.verify(mView).stopProgress(HAS_MORE);

    }

    @After
    public void tearDown() {
        mPresenter.detachView();
    }

}
