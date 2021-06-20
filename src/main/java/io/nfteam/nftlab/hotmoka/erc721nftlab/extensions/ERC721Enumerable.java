package io.nfteam.nftlab.hotmoka.erc721nftlab.extensions;

import io.nfteam.nftlab.hotmoka.erc721nftlab.ERC721;
import io.takamaka.code.lang.*;
import io.takamaka.code.math.UnsignedBigInteger;
import io.takamaka.code.util.*;

import java.util.function.Supplier;

abstract public class ERC721Enumerable extends ERC721 implements IERC721EnumerableView {

  // Mapping from owner to list of owned token IDs
  private final StorageMap<Contract, StorageMap<UnsignedBigInteger, UnsignedBigInteger>> ownedTokens =
    new StorageTreeMap<>();

  // Mapping from token ID to index of the owner tokens list
  private final StorageMap<UnsignedBigInteger, UnsignedBigInteger> ownedTokensIndex = new StorageTreeMap<>();

  // Array with all token ids, used for enumeration
  private final StorageMap<UnsignedBigInteger, UnsignedBigInteger> allTokens = new StorageTreeMap<>();

  // Mapping from token id to position in the allTokens array
  private final StorageMap<UnsignedBigInteger, UnsignedBigInteger> allTokensIndex = new StorageTreeMap<>();

  public ERC721Enumerable(String name, String symbol) {
    super(name, symbol);
  }

  public ERC721Enumerable(String name, String symbol, boolean generateEvents) {
    super(name, symbol, generateEvents);
  }


  @Override
  public @View
  UnsignedBigInteger totalSupply() {
    return UnsignedBigInteger.valueOf(allTokens.size());
  }

  @Override
  public @View
  UnsignedBigInteger tokenOfOwnerByIndex(Contract owner, UnsignedBigInteger index) {
    Takamaka.require(index.compareTo(balanceOf(owner)) < 0, "ERC721Enumerable: owner index out of bounds");
    return ownedTokens.getOrDefault(owner, StorageTreeMap::new).getOrDefault(index, (UnsignedBigInteger) null);
  }

  @Override
  public @View
  UnsignedBigInteger tokenByIndex(UnsignedBigInteger index) {
    Takamaka.require(index.compareTo(totalSupply()) < 0, "ERC721Enumerable: global index out of bounds");

    return allTokens.get(index.toBigInteger().intValue());
  }

  @Override
  protected void _beforeTokenTransfer(Contract from, Contract to, UnsignedBigInteger tokenId) {
    if (from == null) {
      _addTokenToAllTokensEnumeration(tokenId);
    } else if (from != to) {
      _removeTokenFromOwnerEnumeration(from, tokenId);
    }
    if (to == null) {
      _removeTokenFromAllTokensEnumeration(tokenId);
    } else if (to != from) {
      _addTokenToOwnerEnumeration(to, tokenId);
    }
  }

  /**
   * Private function to add a token to this extension's ownership-tracking data structures.
   * @param to address representing the new owner of the given token ID
   * @param tokenId uint256 ID of the token to be added to the tokens list of the given address
   */
  private
  void _addTokenToOwnerEnumeration(Contract to, UnsignedBigInteger tokenId) {
    UnsignedBigInteger length = balanceOf(to);
    ownedTokens
      .computeIfAbsent(to, (Supplier<StorageMap<UnsignedBigInteger, UnsignedBigInteger>>) StorageTreeMap::new)
      .put(length, tokenId);
    ownedTokensIndex.put(tokenId, length);
  }

  /**
   * Private function to add a token to this extension's token tracking data structures.
   * @param tokenId uint256 ID of the token to be added to the tokens list
   */
  private
  void _addTokenToAllTokensEnumeration(UnsignedBigInteger tokenId) {
    UnsignedBigInteger index = UnsignedBigInteger.valueOf(allTokens.size());
    allTokensIndex.put(tokenId, UnsignedBigInteger.valueOf(allTokens.size()));
    allTokens.put(index, tokenId);
  }

  /**
   * Private function to remove a token from this extension's ownership-tracking data structures. Note that
   * while the token is not assigned a new owner, the `_ownedTokensIndex` mapping is _not_ updated: this allows for
   * gas optimizations e.g. when performing a transfer operation (avoiding double writes).
   * This has O(1) time complexity, but alters the order of the _ownedTokens array.
   * @param from address representing the previous owner of the given token ID
   * @param tokenId uint256 ID of the token to be removed from the tokens list of the given address
   */
  private
  void _removeTokenFromOwnerEnumeration(Contract from, UnsignedBigInteger tokenId) {
    // To prevent a gap in from's tokens array, we store the last token in the index of the token to delete, and
    // then delete the last slot (swap and pop).

    UnsignedBigInteger lastTokenIndex = balanceOf(from).subtract(ONE);
    UnsignedBigInteger tokenIndex = ownedTokensIndex.getOrDefault(tokenId, ZERO);

    // When the token to delete is the last token, the swap operation is unnecessary
    if (!tokenIndex.equals(lastTokenIndex)) {
      UnsignedBigInteger lastTokenId = ownedTokens.getOrDefault(from, StorageTreeMap::new).getOrDefault(lastTokenIndex, ZERO);

      ownedTokens
        .computeIfAbsent(from,  (Supplier<StorageMap<UnsignedBigInteger, UnsignedBigInteger>>) StorageTreeMap::new)
        .put(tokenIndex, lastTokenId); // Move the last token to the slot of the to-delete token
      ownedTokensIndex.put(lastTokenId, tokenIndex); // Update the moved token's index
    }

    // This also deletes the contents at the last position of the array
    ownedTokensIndex.remove(tokenId);
    ownedTokens.getOrDefault(from, StorageTreeMap::new).remove(lastTokenIndex);
  }

  /**
   * Private function to remove a token from this extension's token tracking data structures.
   * This has O(1) time complexity, but alters the order of the _allTokens array.
   * @param tokenId uint256 ID of the token to be removed from the tokens list
   */
  private
  void _removeTokenFromAllTokensEnumeration(UnsignedBigInteger tokenId) {
    // To prevent a gap in the tokens array, we store the last token in the index of the token to delete, and
    // then delete the last slot (swap and pop).

    UnsignedBigInteger lastTokenIndex = UnsignedBigInteger.valueOf(allTokens.size() - 1);
    UnsignedBigInteger tokenIndex = allTokensIndex.getOrDefault(tokenId, (UnsignedBigInteger) null);

    Takamaka.require(tokenIndex != null, "ERC721Enumerable: Token not exists.");

    // When the token to delete is the last token, the swap operation is unnecessary. However, since this occurs so
    // rarely (when the last minted token is burnt) that we still do the swap here to avoid the gas cost of adding
    // an 'if' statement (like in _removeTokenFromOwnerEnumeration)
    UnsignedBigInteger lastTokenId = allTokens.get(lastTokenIndex.toBigInteger().intValue());

    allTokens.remove(lastTokenId);
    allTokensIndex.put(lastTokenId, tokenIndex); // Update the moved token's index

    // This also deletes the contents at the last position of the array
    allTokens.remove(allTokensIndex.get(tokenId));
    allTokensIndex.remove(tokenId);
  }

  @Exported
  protected class ERC721EnumerableSnapshot extends ERC721.ERC721Snapshot implements IERC721EnumerableView {
    private final StorageMapView<Contract, StorageMap<UnsignedBigInteger, UnsignedBigInteger>> ownedTokens =
      ERC721Enumerable.this.ownedTokens;

    // Mapping from token ID to index of the owner tokens list
    private final StorageMapView<UnsignedBigInteger, UnsignedBigInteger> ownedTokensIndex =
      ERC721Enumerable.this.ownedTokensIndex;

    // Array with all token ids, used for enumeration
    private final StorageMapView<UnsignedBigInteger, UnsignedBigInteger> allTokens = ERC721Enumerable.this.allTokens;

    // Mapping from token id to position in the allTokens array
    private final StorageMapView<UnsignedBigInteger, UnsignedBigInteger> allTokensIndex =
      ERC721Enumerable.this.allTokensIndex;

    @Override
    public UnsignedBigInteger totalSupply() {
      return UnsignedBigInteger.valueOf(allTokens.size());
    }

    @Override
    public UnsignedBigInteger tokenOfOwnerByIndex(Contract owner, UnsignedBigInteger index) {
      Takamaka.require(index.compareTo(balanceOf(owner)) < 0, "ERC721Enumerable: owner index out of bounds");

      return ownedTokens.getOrDefault(owner, StorageTreeMap::new).getOrDefault(index, (UnsignedBigInteger) null);
    }

    @Override
    public UnsignedBigInteger tokenByIndex(UnsignedBigInteger index) {
      return allTokens.get(index.toBigInteger().intValue());
    }

    @Override
    public @View
    IERC721EnumerableView snapshot() {
      return this;
    }
  }

  public @View
  IERC721EnumerableView snapshot() {
    return new ERC721EnumerableSnapshot();
  }
}
