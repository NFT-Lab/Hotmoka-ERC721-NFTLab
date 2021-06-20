package io.nfteam.nftlab.hotmoka.erc721nftlab;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721View {
  /**
   * @param owner: account whose balance you want to check
   * @return the number of tokens owned by (@code owner)
   */
  @View
  UnsignedBigInteger balanceOf(Contract owner);

  /**
   * @param tokenId: token index whose owner you want to check
   * @return the owner of token with index at (@code tokenId)
   */
  @View
  Contract ownerOf(UnsignedBigInteger tokenId);

  /**
   * @param tokenId: token index whose account approved you want to know
   * @return the account approved for (@code tokenId)
   */
  @View
  Contract getApproved(UnsignedBigInteger tokenId);

  /**
   * @param owner: owner of a token
   * @param operator: account whose you want to check if it's approved
   * @return if the (@code operator) is allowed to manage all of the assets of (@code owner)
   */
  @View
  boolean isApprovedForAll(Contract owner, Contract operator);

  IERC721View snapshot();
}
