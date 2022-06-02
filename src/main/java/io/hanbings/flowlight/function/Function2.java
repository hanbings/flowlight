/*
 * Copyright 2022 Flowlight
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.hanbings.flowlight.function;

@FunctionalInterface
@SuppressWarnings("unused")
public interface Function2<T1, T2, R> {
    R apply(T1 t1, T2 t2);

    default Function2<T1, T2, R> andThen(Function1<? super R, ? extends R> after) {
        return (t1, t2) -> after.apply(apply(t1, t2));
    }

    default Function2<T1, T2, R> compose(Function1<? super T1, ? extends T1> before) {
        return (t1, t2) -> apply(before.apply(t1), t2);
    }
}
