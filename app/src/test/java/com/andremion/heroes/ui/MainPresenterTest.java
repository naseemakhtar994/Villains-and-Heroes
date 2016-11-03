package com.andremion.heroes.ui;

import com.andremion.heroes.api.MarvelApi;
import com.andremion.heroes.api.MarvelCallback;
import com.andremion.heroes.api.data.CharacterVO;
import com.andremion.heroes.api.json.CharacterData;
import com.andremion.heroes.api.json.CharacterDataContainer;
import com.andremion.heroes.api.json.CharacterDataWrapper;
import com.andremion.heroes.api.json.SectionList;
import com.andremion.heroes.api.json.Url;
import com.andremion.heroes.ui.home.MainContract;
import com.andremion.heroes.ui.home.MainPresenter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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
//        mPresenter.attachView(mView);
    }

    @Test
    public void test() {

        final int OFFSET = 0;
        final int TOTAL = MarvelApi.MAX_FETCH_LIMIT;
        final String ATTRIBUTION = "";
        final boolean HAS_MORE = true;
        CharacterDataWrapper data = new CharacterDataWrapper();
        data.attributionText = ATTRIBUTION;
        CharacterDataContainer dataContainer = new CharacterDataContainer();
        dataContainer.offset = OFFSET;
        dataContainer.total = TOTAL;
        CharacterData characterData = Mockito.mock(CharacterData.class);
        characterData.urls = new Url[]{};
        characterData.comics = new SectionList();
        characterData.series = new SectionList();
        characterData.stories = new SectionList();
        characterData.events = new SectionList();
        dataContainer.results = new CharacterData[]{characterData};
        data.data = dataContainer;

        mPresenter.attachView(mView);

        InOrder inOrder = inOrder(mView);
        inOrder.verify(mView).showInfoDialog();
        inOrder.verify(mView).showProgress();

        verify(mMarvelApi).listCharacters(eq(OFFSET), mListCharactersCallback.capture());
        mListCharactersCallback.getValue().onResult(data);

        inOrder.verify(mView).showResult(ArgumentMatchers.<CharacterVO>anyList());
        inOrder.verify(mView).showAttribution(eq(ATTRIBUTION));
        inOrder.verify(mView).stopProgress(eq(HAS_MORE));

    }

    @After
    public void tearDown() {
//        mPresenter.detachView();
    }

}
