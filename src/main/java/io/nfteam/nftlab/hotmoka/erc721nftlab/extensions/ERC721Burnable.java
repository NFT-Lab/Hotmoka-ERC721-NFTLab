package io.nfteam.nftlab.hotmoka.erc721nftlab.extensions;

import io.nfteam.nftlab.hotmoka.erc721nftlab.ERC721;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.lang.Takamaka;
import io.takamaka.code.math.UnsignedBigInteger;

abstract public class ERC721Burnable extends ERC721
{
  public
  ERC721Burnable(String name, String symbol) {
    super(name, symbol);
  }

  public
  ERC721Burnable(String name, String symbol, boolean generateEvents) {
    super(name, symbol, generateEvents);
  }

  /**
   * Burns (@code tokenId)`. See (@link ERC721._burn).
   *
   * Requirements:
   *
   * - The caller must own (@code tokenId) or be an approved operator.
   */
  public @FromContract
  void burn(UnsignedBigInteger tokenId) {
    Takamaka.require(_isApprovedOrOwner(caller(), tokenId), "ERC721Burnable: caller is not owner nor approved");

    _burn(tokenId);
  }
}
