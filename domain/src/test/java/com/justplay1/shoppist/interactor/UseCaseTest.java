/*
 * Copyright (C) 2016 Mkhytar Mkhoian
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.justplay1.shoppist.interactor;

import com.justplay1.shoppist.executor.PostExecutionThread;
import com.justplay1.shoppist.executor.ThreadExecutor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;

public class UseCaseTest {

    private UseCaseTestClass<Integer> useCase;

    @Mock
    private ThreadExecutor mockThreadExecutor;
    @Mock
    private PostExecutionThread mockPostExecutionThread;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.useCase = new UseCaseTestClass<>(mockThreadExecutor, mockPostExecutionThread);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void buildUseCaseObservable_ReturnCorrectResult() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
        TestScheduler testScheduler = new TestScheduler();
        given(mockPostExecutionThread.getScheduler()).willReturn(testScheduler);

        useCase.get().subscribe(testSubscriber);

        assertThat(testSubscriber.getOnNextEvents().size(), is(0));
    }

    @Test
    public void subscription_WhenExecutingUseCase() {
        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

        Subscription subscription = useCase.get().subscribe(testSubscriber);
        subscription.unsubscribe();

        assertThat(testSubscriber.isUnsubscribed(), is(true));
    }

    private static class UseCaseTestClass<T> extends UseCase<T> {

        UseCaseTestClass(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
            super(threadExecutor, postExecutionThread);
        }

        @Override
        protected Observable<T> buildUseCaseObservable() {
            return Observable.empty();
        }
    }
}
