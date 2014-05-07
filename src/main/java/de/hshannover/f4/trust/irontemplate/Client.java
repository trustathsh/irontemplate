/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of irontemplate, version 0.0.1,
 * implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.irontemplate;

import static de.hshannover.f4.trust.ifmapj.metadata.MetadataWrapper.metadata;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.hshannover.f4.trust.ifmapj.IfmapJ;
import de.hshannover.f4.trust.ifmapj.channel.SSRC;
import de.hshannover.f4.trust.ifmapj.config.BasicAuthConfig;
import de.hshannover.f4.trust.ifmapj.exception.IfmapErrorResult;
import de.hshannover.f4.trust.ifmapj.exception.IfmapException;
import de.hshannover.f4.trust.ifmapj.exception.InitializationException;
import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.messages.PublishElement;
import de.hshannover.f4.trust.ifmapj.messages.PublishRequest;
import de.hshannover.f4.trust.ifmapj.messages.Requests;
import de.hshannover.f4.trust.ifmapj.messages.ResultItem;
import de.hshannover.f4.trust.ifmapj.messages.SearchRequest;
import de.hshannover.f4.trust.ifmapj.messages.SearchResult;
import de.hshannover.f4.trust.ifmapj.metadata.Metadata;
import de.hshannover.f4.trust.ifmapj.metadata.VendorSpecificMetadataFactory;

/**
 * Example class that demonstrates basic ifmapj-features: 1:) connect to a MAP
 * server 2.) create Vendor Specific Metadata 3.) create an Extended Identifier
 * 4.) publish these to an MAPS 5.) search for these on the MAPS 6.) print the
 * information of the metadata 7.) close the connection
 */
public class Client {

	private static final String VERSION = "${project.version}";

	private static Logger LOGGER = Logger.getLogger(Client.class);

	public static void main(String[] args) {
		LOGGER.info("Starting irontemplate version " + VERSION);

		String url = "http://localhost:8443";
		String username = "test";
		String password = "test";
		String trustStorePath = "/keystore/irontemplate.jks";
		String trustStorePassword = "irontemplate";
		boolean threadSafe = true;

		BasicAuthConfig config = new BasicAuthConfig(url, username, password,
				trustStorePath, trustStorePassword, threadSafe);

		try {
			LOGGER.info("Creating SSRC");
			SSRC ssrc = IfmapJ.createSsrc(config);

			LOGGER.info("Opening session");
			ssrc.newSession();
			PublishRequest req = Requests.createPublishReq();

			VendorSpecificMetadataFactory factory = IfmapJ
					.createVendorSpecificMetadataFactory();
			String vendorSpecificMetadataXml = "<custom:part-of ifmap-cardinality=\"singleValue\" "
					+ "xmlns:custom=\"http://www.example.com/vendor-metadata\"> "
					+ "</custom:part-of>";
			Document outgoingMetadata = factory
					.createMetadata(vendorSpecificMetadataXml);

			String extendedIdentifierXml = "<ns:network "
					+ "administrative-domain=\"\" "
					+ "address=\"192.168.1.1\" "
					+ "type=\"IPv4\" "
					+ "netmask=\"255.255.255.0\" "
					+ "xmlns:ns=\"http://www.example.com/extended-identifiers\" "
					+ "/>";
			Identifier identifier = Identifiers
					.createExtendedIdentity(extendedIdentifierXml);

			PublishElement pe = Requests.createPublishUpdate(identifier,
					outgoingMetadata);
			req.addPublishElement(pe);

			LOGGER.info("Publishing metadata");
			ssrc.publish(req);

			SearchRequest searchReq = Requests.createSearchReq();
			searchReq.setStartIdentifier(identifier);
			searchReq.setMaxDepth(1);

			LOGGER.info("Search for metadata");
			SearchResult searchResult = ssrc.search(searchReq);

			LOGGER.info("Printing search results");
			for (ResultItem resultItem : searchResult.getResultItems()) {
				Identifier[] identifiers = resultItem.getIdentifier();
				if (identifiers[0] != null) {
					LOGGER.info("Identifier: " + identifiers[0]);
				}

				if (identifiers[1] != null) {
					LOGGER.info("Identifier: " + identifiers[1]);
				}

				for (Document metadata : resultItem.getMetadata()) {
					Metadata incomingMetadata = metadata(metadata);

					String publisherId = incomingMetadata.getPublisherId();
					String publishTimestamp = incomingMetadata
							.getPublishTimestamp();
					String cardinality = incomingMetadata.getCardinality();
					String localname = incomingMetadata.getLocalname();
					String typename = incomingMetadata.getTypename();

					LOGGER.info("Publisher ID:      " + publisherId);
					LOGGER.info("Publish timestamp: " + publishTimestamp);
					LOGGER.info("Cardinality:       " + cardinality);
					LOGGER.info("Local name:        " + localname);
					LOGGER.info("Typename:          " + typename);
				}
			}

			LOGGER.info("Ending session");
			ssrc.endSession();
		} catch (InitializationException e) {
			e.printStackTrace();
		} catch (IfmapErrorResult e) {
			e.printStackTrace();
		} catch (IfmapException e) {
			e.printStackTrace();
		}
	}
}
