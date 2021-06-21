package io.nfteam.nftlab.hotmoka.erc721_customized.extensions;

import io.nfteam.nftlab.hotmoka.erc721_customized.IERC721View;
import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.View;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721EnumerableView extends IERC721View {
  /**
   * Returns the total amount of tokens stored by the contract.
   */
  @View
  UnsignedBigInteger totalSupply();

  /**
   * Returns a token ID owned by `owner` at a given `index` of its token list.
   * Use along with {balanceOf} to enumerate all of ``owner``'s tokens.
   */
  @View
  UnsignedBigInteger tokenOfOwnerByIndex(Contract owner, UnsignedBigInteger index);

  /**
   * Returns a token ID at a given `index` of all the tokens stored by the contract.
   * Use along with {totalSupply} to enumerate all tokens.
   */
  @View
  UnsignedBigInteger tokenByIndex(UnsignedBigInteger index);

  @View
  IERC721EnumerableView snapshot();
}
