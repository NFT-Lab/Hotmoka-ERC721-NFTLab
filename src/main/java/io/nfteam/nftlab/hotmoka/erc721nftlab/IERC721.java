package io.nfteam.nftlab.hotmoka.erc721nftlab;

import io.takamaka.code.lang.Contract;
import io.takamaka.code.lang.Event;
import io.takamaka.code.lang.FromContract;
import io.takamaka.code.math.UnsignedBigInteger;

public interface IERC721 extends IERC721View {
  /**
   * Safely transfers (@code tokenId) token from (@code from) to (@code to), checking first that contract recipients
   * are aware of the ERC721 protocol to prevent tokens from being forever locked.
   *
   * Emits a (@link IERC721.Transfer) event.
   *
   * Requirements:
   * - (@code from) cannot be the zero address.
   * - (@code to) cannot be the zero address.
   * - (@code tokenId) token must exist and be owned by (@code from).
   * - If the caller is not (@code from), it must be have been allowed to move this token by either {approve} or {setApprovalForAll}.
   * - If (@code to) refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
   */
  @FromContract
  void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId);

  /**
   * Transfers (@code tokenId) (@code token) from (@code from) to (@code to).
   *
   * Emits a {IERC721.Transfer} event.
   *
   * WARNING: Usage of this method is discouraged, use {safeTransferFrom} whenever possible.
   *
   * Requirements:
   * - (@code from) cannot be the zero address.
   * - (@code to) cannot be the zero address.
   * - (@code tokenId) token must be owned by (@code from).
   * - If the caller is not (@code from), it must be approved to move this token by either {approve} or {setApprovalForAll}.
   */
  @FromContract
  void transferFrom(Contract from, Contract to, UnsignedBigInteger tokenId);

    /**
     * Gives permission to (@code to) to transfer tokenId @code token to another account.
     * The approval is cleared when the token is transferred.
     *
     * Only a single account can be approved at a time, so approving the zero address clears previous approvals.
     *
     * Emits an {IERC721.Approval} event.
     *
     * Requirements:
     * - The caller must own the token or be an approved operator.
     * - (@code tokenId) must exist.
     */
  @FromContract
  void approve(Contract to, UnsignedBigInteger tokenId);

  /**
   * Approve or remove (@code operator) as an operator for the caller.
   * Operators can call {transferFrom} or {safeTransferFrom} for any token owned by the caller.
   *
   * Requirements:
   *
   * - The (@code operator) cannot be the caller.
   *
   * Emits an {IERC721.ApprovalForAll} event.
   */
  @FromContract
  void setApprovalForAll(Contract operator, boolean _approved);

  /**
   * Safely transfers (@code tokenId) token from (@code from) to @code to.
   *
   * Requirements:
   *
   * - (@code from) cannot be the zero address.
   * - (@code to) cannot be the zero address.
   * - (@code tokenId) token must exist and be owned by (@code from).
   * - If the caller is not `from`, it must be approved to move this token by either {approve} or {setApprovalForAll}.
   * - If `to` refers to a smart contract, it must implement {IERC721Receiver-onERC721Received}, which is called upon a safe transfer.
   *
   * Emits a {IERC721.Transfer} event.
   */
  @FromContract
  void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte[] data);

  /**
   * Emitted when (@code tokenId) token is transferred from (@code from) to (@code to).
   */
  class Transfer extends Event {
    public final Contract from;
    public final Contract to;
    public final UnsignedBigInteger tokenId;

    @FromContract
    public Transfer(Contract from, Contract to, UnsignedBigInteger tokenId) {
      this.from = from;
      this.to = to;
      this.tokenId = tokenId;
    }
  }

  /**
   * Emitted when (@code owner) enables (@code approved) to manage the (@code tokenId) token.
   */
  class Approval extends Event {
    public final Contract owner;
    public final Contract approved;
    public final UnsignedBigInteger tokenId;

    @FromContract
    public Approval(Contract owner, Contract approved, UnsignedBigInteger tokenId) {
      this.owner = owner;
      this.approved = approved;
      this.tokenId = tokenId;
    }
  }

  /**
   * Emitted when (@code owner) enables or disables (@code approved)) (@code operator) to manage all of its assets.
   */
  class ApprovalForAll extends Event {
    public final Contract owner;
    public final Contract operator;
    public final boolean approved;

    @FromContract
    public ApprovalForAll(Contract owner, Contract operator, boolean approved) {
      this.owner = owner;
      this.operator = operator;
      this.approved = approved;
    }
  }
}
