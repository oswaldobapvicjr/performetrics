/*
 * Copyright 2021 obvj.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * <p>
 * Provides convenient classes for extracting performance metrics of Java code.
 * </p>
 *
 * <p>
 * The top-level package contains the <b>Counter</b> and <b>TimingSession</b> classes,
 * which are the base for computing the metrics of wall-clock time, CPU time, user time,
 * and system time.
 * </p>
 *
 * <p>
 * This package also contains the <b>Stopwatch</b> class that supports all of the
 * abovementioned counters.
 * </p>
 *
 * <p>
 * Global configuration parameters, such as default conversion mode, time unit, and
 * precision, can be set up using the <b>Performetrics</b> facade.
 * </p>
 */
package net.obvj.performetrics;
