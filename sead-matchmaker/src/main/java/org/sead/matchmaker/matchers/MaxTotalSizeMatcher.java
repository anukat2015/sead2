/*
 *
 * Copyright 2015 University of Michigan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * @author myersjd@umich.edu
 */

package org.sead.matchmaker.matchers;

import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.sead.matchmaker.Matcher;
import org.sead.matchmaker.RuleResult;

public class MaxTotalSizeMatcher implements Matcher {

	public RuleResult runRule(Document aggregation, Object rightsHolders, BasicBSONList affiliations,
			Document preferences, Document statsDocument, Document profile, Object context) {
		RuleResult result = new RuleResult();

		try {

			long max = Long.parseLong(statsDocument.getString("Total Size"));
			long repoMax = Long.parseLong(profile.getString("Total Size"));
			if (max > repoMax) {
				result.setResult(-1, "Total size exceeds maximum allowed ("
						+ repoMax + ").");
			} else {
				result.setResult(1, "Total size is acceptable (<=" + repoMax
						+ ").");
			}
		} catch (NullPointerException npe) {
			// Just return untriggered result
			System.out.println("Missing info in TotalSize rule"
					+ npe.getLocalizedMessage());
		} catch (NumberFormatException nfe) {
			// Just return untriggered result
			System.out.println("Missing info in MaxDatasetSize rule for repo: "
					+ profile.getString("orgidentifier") + " : "
					+ nfe.getLocalizedMessage());
		}
		return result;

	}

	public String getName() {
		return "Maximum Total Size";
	}

	public Document getDescription() {
		return new Document("Rule Name", getName())
				.append("Repository Trigger",
						" \"Total Size\": \"tag:tupeloproject.org,2006:/2.0/files/length\" : long size (Bytes) as String")
				.append("Publication Trigger",
						" \"Total Size\": \"tag:tupeloproject.org,2006:/2.0/files/length\" : long size (Bytes) as String, in \"Aggregation Statistics\": \"http://sead-data.net/terms/publicationstatistics\", in publication request");
	}
}
