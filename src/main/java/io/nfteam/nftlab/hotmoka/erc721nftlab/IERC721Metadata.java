package io.nfteam.nftlab.hotmoka.erc721nftlab;

import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721Metadata extends IERC721 {
  /**
   * Returns the token collection name.
   */
  @View
  String name();

  /**
   * Returns the token collection symbol.
   */
  @View
  String symbol();

  /**
   * Returns the Uniform Resource Identifier (URI) for (@code tokenId) token.
   */
  @View
  String tokenURI(UnsignedBigInteger tokenId);
}
