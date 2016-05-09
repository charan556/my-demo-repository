package demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.domain.Sentence;
import demo.domain.Word;
import rx.Observable;

@Service
public class SentenceServiceImpl implements SentenceService {

	@Autowired
	WordService wordService;

	public String buildSentence() {
		Sentence sentence = new Sentence();
		List<Observable<Word>> observables = createObservables();
		CountDownLatch latch = new CountDownLatch(observables.size());
		Observable.merge(observables).subscribe((word) -> {
			sentence.add(word);
			latch.countDown();
		});
		waitForAll(latch);
		return sentence.toString();
	}

	private void waitForAll(CountDownLatch latch) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private List<Observable<Word>> createObservables() {
		List<Observable<Word>> observables = new ArrayList<>();
		observables.add(wordService.getSubject());
		observables.add(wordService.getVerb());
		observables.add(wordService.getArticle());
		observables.add(wordService.getAdjective());
		observables.add(wordService.getNoun());
		return observables;
	}

}
