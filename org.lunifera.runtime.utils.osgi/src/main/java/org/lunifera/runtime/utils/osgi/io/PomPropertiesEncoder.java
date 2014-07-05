package org.lunifera.runtime.utils.osgi.io;

/*
 * #%L
 * Lunifera Subsystems - Runtime Utilities for OSGi
 * %%
 * Copyright (C) 2012 - 2014 C4biz Softwares ME, Loetz KG
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.DatatypeConverter;

import com.google.common.io.ByteStreams;

/**
 * 
 * @author Cristiano Gavião
 * @since 0.0.1
 */
public class PomPropertiesEncoder {

    public static String encodeCompositeArtifactsXml() throws IOException {
        InputStream in = null;
        try {

            // Charsets.UTF_8;
            in = PomPropertiesEncoder.class
                    .getResourceAsStream("/template.composite.p2/compositeArtifacts.xml");

            byte[] message = ByteStreams.toByteArray(in);
            String encoded = DatatypeConverter.printBase64Binary(message);
            System.out.println("encoded compositeArtifacts: " + encoded);
            return encoded;
        } finally {
            if (in != null) {
                in.close();
            }
            in = null;
        }
    }

    public static String encodeCompositeContentXml() throws IOException {
        InputStream in = null;
        try {

            // Charsets.UTF_8;
            in = PomPropertiesEncoder.class
                    .getResourceAsStream("/template.composite.p2/compositeContent.xml");

            byte[] message = ByteStreams.toByteArray(in);
            String encoded = DatatypeConverter.printBase64Binary(message);
            System.out.println("encoded compositeContent: " + encoded);
            return encoded;
        } finally {
            if (in != null) {
                in.close();
            }
            in = null;
        }
    }

    public static String encodeP2Index() throws IOException {
        InputStream in = null;
        try {

            // Charsets.UTF_8;
            in = PomPropertiesEncoder.class
                    .getResourceAsStream("/template.composite.p2/p2.index");

            byte[] message = ByteStreams.toByteArray(in);
            String encoded = DatatypeConverter.printBase64Binary(message);
            System.out.println("encoded P2Index: " + encoded);
            return encoded;
        } finally {
            if (in != null) {
                in.close();
            }
            in = null;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {

            encodeCompositeArtifactsXml();

            System.out.println();
            System.out.println();

            encodeCompositeContentXml();

            System.out.println();
            System.out.println();

            encodeP2Index();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));

        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    public String showDecodedString(String encodedString) {
        byte[] decoded = DatatypeConverter.parseBase64Binary(encodedString);
        String decodedString = new String(decoded);
        System.out.println("decoded:" + decodedString);
        return decodedString;
    }

}
