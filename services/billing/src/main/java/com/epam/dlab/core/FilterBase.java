/***************************************************************************

Copyright (c) 2016, EPAM SYSTEMS INC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

****************************************************************************/

package com.epam.dlab.core;

import java.util.List;

import com.epam.dlab.core.parser.ParserBase;
import com.epam.dlab.core.parser.ReportLine;
import com.epam.dlab.exception.InitializationException;
import com.epam.dlab.exception.ParseException;
import com.google.common.base.MoreObjects.ToStringHelper;

/** Abstract module of filtering. 
 * See description of {@link ModuleBase} how to create your own filter.
 */
public abstract class FilterBase extends ModuleBase {
	
	/** Parser. */
	private ParserBase parser;
	
	/** Return parser. */
	public ParserBase getParser() {
		return parser;
	}
	
	/** Set parser. */
	public void setParser(ParserBase parser) {
		this.parser = parser;
	}
	
	
	/** Initialize the filter.
	 * @throws InitializationException
	 */
	public abstract void initialize() throws InitializationException;

	/** Return the line for parsing if line is accepted and may be parsed,
	 * otherwise return <b>null</b>.
	 * @param line the source line.
	 * @throws ParseException
	 */
	public abstract String canParse(String line) throws ParseException;

	/** Return the list of values for transformation if value is accepted and may be transformed,
	 * otherwise return <b>null</b>.
	 * @param row the list of values.
	 * @throws ParseException
	 */
	public abstract List<String> canTransform(List<String> row) throws ParseException;

	/** Return the row of billing report if row is accepted and may be written to target,
	 * otherwise return <b>null</b>.
	 * @param row the report line.
	 * @throws ParseException
	 */
	public abstract ReportLine canAccept(ReportLine row) throws ParseException;
	
	@Override
	public ToStringHelper toStringHelper(Object self) {
		return super.toStringHelper(self)
				.add("parser", (parser == null ? null : parser.getType()));
	}
}
