package com.application;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Converter class, originally used for gif creation
 */
public class Converter {
    private javax.imageio.ImageWriter gifWriter;
    private javax.imageio.stream.ImageOutputStream imageOutputStream;
    private javax.imageio.metadata.IIOMetadata metadata;

    public Converter(
            ImageOutputStream outputStream,
            int imageType,
            int delayMS,
            boolean loop) throws IOException {

        gifWriter = javax.imageio.ImageIO.getImageWritersBySuffix("gif").next();
        javax.imageio.ImageWriteParam params = gifWriter.getDefaultWriteParam();
        metadata = gifWriter.getDefaultImageMetadata(
                new javax.imageio.ImageTypeSpecifier(
                        new BufferedImage(1, 1, imageType)), params);

        String metaFormat = metadata.getNativeMetadataFormatName();
        javax.imageio.metadata.IIOMetadataNode root =
                (javax.imageio.metadata.IIOMetadataNode) metadata.getAsTree(metaFormat);

        javax.imageio.metadata.IIOMetadataNode graphicsControlExtensionNode =
                new javax.imageio.metadata.IIOMetadataNode("GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delayMS / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        root.appendChild(graphicsControlExtensionNode);

        if (loop) {
            javax.imageio.metadata.IIOMetadataNode appExtensionsNode =
                    new javax.imageio.metadata.IIOMetadataNode("ApplicationExtensions");
            javax.imageio.metadata.IIOMetadataNode appExtension =
                    new javax.imageio.metadata.IIOMetadataNode("ApplicationExtension");
            appExtension.setAttribute("applicationID", "NETSCAPE");
            appExtension.setAttribute("authenticationCode", "2.0");
            appExtension.setUserObject(new byte[]{1, 0, 0});
            appExtensionsNode.appendChild(appExtension);
            root.appendChild(appExtensionsNode);
        }

        metadata.setFromTree(metaFormat, root);
        imageOutputStream = outputStream;
        gifWriter.setOutput(imageOutputStream);
        gifWriter.prepareWriteSequence(null);
    }

    public void writeToSequence(BufferedImage img) throws IOException {
        gifWriter.writeToSequence(
                new javax.imageio.IIOImage(img, null, metadata),
                gifWriter.getDefaultWriteParam());
    }

    public void close() throws IOException {
        gifWriter.endWriteSequence();
        imageOutputStream.close();
    }
}