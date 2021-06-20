package io.nfteam.nftlab.hotmoka.erc721_customized.extensions;

import io.nfteam.nftlab.hotmoka.erc721_customized.ERC721;
import io.takamaka.code.lang.*;
import io.takamaka.code.math.UnsignedBigInteger;
import io.takamaka.code.util.StorageMap;
import io.takamaka.code.util.StorageMapView;
import io.takamaka.code.util.StorageTreeMap;

abstract public class ERC721URIStorage extends ERC721 implements IERC721URIStorageView {
  private final StorageMap<UnsignedBigInteger, String> tokenURIs = new StorageTreeMap<>();

  public ERC721URIStorage(String name, String symbol) {
    super(name, symbol);
  }

  public ERC721URIStorage(String name, String symbol, boolean generateEvents) {
    super(name, symbol, generateEvents);
  }

  @View
  public String tokenURI(UnsignedBigInteger tokenId) {
    Takamaka.require(_exists(tokenId), "ERC721URIStorage: URI query for nonexistent token");

    String tokenURI = tokenURIs.getOrDefault(tokenId, "");
    String base = _baseURI();

    if (base.isEmpty()) {
      return tokenURI;
    }
    if (!tokenURI.isEmpty()) {
      return base + tokenURI;
    }

    return super.tokenURI(tokenId);
  }

  /**
   * Sets (@code _tokenURI) as the tokenURI of (@code tokenId).
   *
   * Requirements:
   *
   * - (@code tokenId) must exist.
   */
  protected void _setTokenURI(UnsignedBigInteger tokenId, String tokenURI) {
    Takamaka.require(_exists(tokenId), "ERC721URIStorage: URI set of nonexistent token");
    tokenURIs.put(tokenId, tokenURI);
  }

  /**
   * Destroys (@code tokenId).
   * The approval is cleared when the token is burned.
   *
   * Requirements:
   * - (@code tokenId) must exist.
   *
   * Emits a (@link ERC721.Transfer) event.
   */
  @Override
  protected void _burn(UnsignedBigInteger tokenId) {
    super._burn(tokenId);

    tokenURIs.remove(tokenId);
  }

  @Exported
  protected class ERC721URIStorageSnapshot extends ERC721Snapshot implements IERC721URIStorageView {
    private final StorageMapView<UnsignedBigInteger, String> tokenURIs = ERC721URIStorage.this.tokenURIs;

    @Override @View
    public String tokenURI(UnsignedBigInteger tokenId) {
      Takamaka.require(_exists(tokenId), "ERC721URIStorage: URI query for nonexistent token");

      String tokenURI = tokenURIs.getOrDefault(tokenId, "");
      String base = _baseURI();

      if (base.isEmpty()) {
        return tokenURI;
      }
      if (!tokenURI.isEmpty()) {
        return base + tokenURI;
      }

      return _baseURI() + tokenId.toString();
    }

    @Override @View
    public IERC721URIStorageView snapshot() {
      return this;
    }
  }

  @Override @View
  public IERC721URIStorageView snapshot() {
    return new ERC721URIStorageSnapshot();
  }
}
