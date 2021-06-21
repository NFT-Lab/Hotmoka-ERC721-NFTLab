package io.nfteam.nftlab.hotmoka.erc721_customized;

import io.takamaka.code.lang.*;
import io.takamaka.code.math.UnsignedBigInteger;
import io.takamaka.code.util.StorageMap;
import io.takamaka.code.util.StorageMapView;
import io.takamaka.code.util.StorageTreeMap;

public class ERC721 extends Contract implements IERC721Metadata {
  private final String name;
  private final String symbol;
  private final boolean generateEvents;
  protected final UnsignedBigInteger ZERO = UnsignedBigInteger.valueOf(0);
  protected final UnsignedBigInteger ONE = UnsignedBigInteger.valueOf(1);

  private final StorageMap<UnsignedBigInteger, Contract> owners = new StorageTreeMap<>();
  private final StorageMap<Contract, UnsignedBigInteger> balances = new StorageTreeMap<>();
  private final StorageMap<UnsignedBigInteger, Contract> tokenApprovals = new StorageTreeMap<>();
  private final StorageMap<Contract, StorageMap<Contract, Boolean>> operatorApprovals = new StorageTreeMap<>();

  public
  ERC721(String name, String symbol) {
    this.name = name;
    this.symbol = symbol;
    this.generateEvents = false;
  }

  public
  ERC721(String name, String symbol, boolean generateEvents) {
    this.name = name;
    this.symbol = symbol;
    this.generateEvents = generateEvents;
  }

  // ============ Getters ============

  @Override @View
  public final String name() {
    return name;
  }

  @Override @View
  public final String symbol() {
    return symbol;
  }

  // ============ Transfer ============

  @Override @FromContract
  public void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {
    safeTransferFrom(from, to, tokenId, "".getBytes());
  }

  @Override @FromContract
  public void transferFrom(Contract from, Contract to, UnsignedBigInteger tokenId) {
    Takamaka.require(_isApprovedOrOwner(caller(), tokenId), "ERC721: transfer caller is not owner nor approved");

    _transfer(from, to, tokenId);
  }

  @Override @FromContract
  public void safeTransferFrom(Contract from, Contract to, UnsignedBigInteger tokenId, byte[] data) {
    Takamaka.require(_isApprovedOrOwner(caller(), tokenId), "ERC721: transfer caller is not owner nor approved");
    _safeTransfer(from, to, tokenId, data);
  }

  protected void _safeTransfer(Contract from, Contract to, UnsignedBigInteger tokenId, byte[] data) {
    _transfer(from, to, tokenId);
    // TODO: Call to _checkOnERC721Received
  }

  protected void _beforeTokenTransfer(Contract from, Contract to, UnsignedBigInteger tokenId) { }

  protected void _transfer(Contract from, Contract to, UnsignedBigInteger tokenId) {
    Takamaka.require(ownerOf(tokenId).equals(from), "ERC721: transfer of token that is not own");
    Takamaka.require(to != null, "ERC721: transfer to the zero address");

    _beforeTokenTransfer(from, to, tokenId);

    // Clear approvals from the previous owner
    _approve(null, tokenId);

    balances.put(from, balanceOf(from).subtract(ONE));
    balances.put(to, balanceOf(to).add(ONE));
    owners.put(tokenId, to);

    event(new Transfer(from, to, tokenId));
  }

  // ============ Approvals ============

  @Override @FromContract
  public void approve(Contract to, UnsignedBigInteger tokenId) {
    Contract owner = ownerOf(tokenId);

    Takamaka.require(!owner.equals(to), "ERC721: approval to current owner");

//    Takamaka.require(caller().equals(owner) || isApprovedForAll(owner, caller()),
//      "ERC721: approve caller is not owner nor approved for all"
//    );

    _approve(to, tokenId);
  }

  private void _approve(Contract to, UnsignedBigInteger tokenId) {
    tokenApprovals.put(tokenId, to);
    event(new Approval(ownerOf(tokenId), to, tokenId));
  }

  @Override @FromContract
  public void setApprovalForAll(Contract operator, boolean _approved) {
    Takamaka.require(operator != caller(), "ERC721: approve to caller");

    operatorApprovals.putIfAbsent(caller(), new StorageTreeMap<>());

    operatorApprovals.get(caller()).put(operator, _approved);

    event(new ApprovalForAll(caller(), operator, _approved));
  }

  @Override @View
  public Contract getApproved(UnsignedBigInteger tokenId) {
    Takamaka.require(_exists(tokenId), "ERC721: approved query for nonexistent token");

    return tokenApprovals.get(tokenId);
  }

  @Override @View
  public final boolean isApprovedForAll(Contract owner, Contract operator) {
    return operatorApprovals.getOrDefault(owner, StorageTreeMap::new).getOrDefault(operator, false);
  }

  protected boolean _isApprovedOrOwner(Contract spender, UnsignedBigInteger tokenId) {
    Takamaka.require(_exists(tokenId), "ERC721: operator query for nonexistent token");
    Contract owner = ownerOf(tokenId);

    return (spender.equals(owner) || getApproved(tokenId).equals(spender) || isApprovedForAll(owner, spender));
  }

  // ============ MINT ============

  protected void _safeMint(Contract to, UnsignedBigInteger tokenId) {
    _safeMint(to, tokenId, "".getBytes());
  }

  protected void _safeMint(Contract to, UnsignedBigInteger tokenId, byte[] data) {
    _mint(to, tokenId);
    // TODO: _checkOnERC721Received call
  }

  protected void _mint(Contract to, UnsignedBigInteger tokenId) {
    Takamaka.require(to != null, "ERC721: mint to the zero address");
    Takamaka.require(!_exists(tokenId), "ERC721: token already minted");
    _beforeTokenTransfer(null, to, tokenId);

    balances.put(to, balanceOf(to).add(ONE));
    owners.put(tokenId, to);

    event(new Transfer(null, to, tokenId));
  }

  // ============ Token URI ============

  @Override @View
  public String tokenURI(UnsignedBigInteger tokenId) {
    Takamaka.require(_exists(tokenId), "ERC721Metadata: URI query for nonexistent token");

    String baseURI = _baseURI();
    return !baseURI.isEmpty()
      ? baseURI + tokenId.toString()
      : "";
  }

  @View
  protected String _baseURI() {
    return "";
  }

  // ============ Balance of ============

  @Override @View
  public UnsignedBigInteger balanceOf(Contract owner) {
    Takamaka.require(owner != null, "ERC721: balance query for the zero address");

    return balances.getOrDefault(owner, ZERO);
  }

  // ============ Owner of ============

  @Override @View
  public Contract ownerOf(UnsignedBigInteger tokenId) {
    Contract owner = owners.get(tokenId);

    Takamaka.require(owner != null, "ERC721: owner query for nonexistent token");

    return owner;
  }

  // ============ Snapshot ============

  @Exported
  protected class ERC721Snapshot extends Storage implements IERC721View {
    private final StorageMapView<UnsignedBigInteger, Contract> owners = ERC721.this.owners.snapshot();
    private final StorageMapView<Contract, UnsignedBigInteger> balances = ERC721.this.balances.snapshot();
    private final StorageMapView<UnsignedBigInteger, Contract> tokenApprovals = ERC721.this.tokenApprovals.snapshot();
    private final StorageMapView<Contract, StorageMap<Contract, Boolean>> operatorApprovals = ERC721.this.operatorApprovals.snapshot();

    @Override @View
    public UnsignedBigInteger balanceOf(Contract owner) {
      return balances.getOrDefault(owner, ZERO);
    }

    @Override @View
    public Contract ownerOf(UnsignedBigInteger tokenId) {
      Contract owner = owners.get(tokenId);

      Takamaka.require(owner != null, "ERC721: owner query for nonexistent token");

      return owner;
    }

    @Override @View
    public Contract getApproved(UnsignedBigInteger tokenId) {
      Takamaka.require(_exists(tokenId), "ERC721: approved query for nonexistent token");

      return tokenApprovals.get(tokenId);
    }

    @Override @View
    public boolean isApprovedForAll(Contract owner, Contract operator) {
      return operatorApprovals.getOrDefault(owner, StorageTreeMap::new).getOrDefault(operator, false);
    }

    @Override @View
    public IERC721View snapshot() {
      return this;
    }
  }

  @Override @View
  public IERC721View snapshot() {
    return new ERC721Snapshot();
  }

  // ============ Burn ============

  protected void _burn(UnsignedBigInteger tokenId) {
    Contract owner = ownerOf(tokenId);

    _beforeTokenTransfer(owner, null, tokenId);

    // Clear approvals
    _approve(null, tokenId);

    balances.put(owner, balanceOf(owner).subtract(ONE));
    owners.remove(tokenId);

    event(new Transfer(owner, null, tokenId));
  }

  // ============ Exists ============

  @View
  protected final boolean _exists(UnsignedBigInteger tokenId) {
    return owners.get(tokenId) != null;
  }

  // ============ Events ============

  /**
   * Generates the given event if events are allowed for this token.
   *
   * @param event the event to generate
   */
  protected final void event(Event event) {
    if (generateEvents) {
      Takamaka.event(event);
    }
  }
}
