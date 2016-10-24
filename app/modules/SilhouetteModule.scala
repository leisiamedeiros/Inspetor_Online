package modules

import com.google.inject.{ AbstractModule, Provides }
import com.google.inject.name.Named
import com.mohiva.play.silhouette.api.{ Environment, EventBus, Silhouette, SilhouetteProvider }
import com.mohiva.play.silhouette.api.actions.{ SecuredErrorHandler, UnsecuredErrorHandler }
import com.mohiva.play.silhouette.api.crypto._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services._
import com.mohiva.play.silhouette.api.util._
import com.mohiva.play.silhouette.crypto.{ JcaCookieSigner, JcaCookieSignerSettings, JcaCrypter, JcaCrypterSettings }
import com.mohiva.play.silhouette.impl.authenticators._
import com.mohiva.play.silhouette.impl.providers._
import com.mohiva.play.silhouette.impl.providers.oauth1._
import com.mohiva.play.silhouette.impl.providers.oauth1.secrets.{ CookieSecretProvider, CookieSecretSettings }
import com.mohiva.play.silhouette.impl.providers.oauth1.services.PlayOAuth1Service
import com.mohiva.play.silhouette.impl.providers.oauth2._
import com.mohiva.play.silhouette.impl.providers.oauth2.state.{ CookieStateProvider, CookieStateSettings, DummyStateProvider }
import com.mohiva.play.silhouette.impl.providers.openid.YahooProvider
import com.mohiva.play.silhouette.impl.providers.openid.services.PlayOpenIDService
import com.mohiva.play.silhouette.impl.services._
import com.mohiva.play.silhouette.impl.util._
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.openid.OpenIdClient
import play.api.libs.ws.WSClient

import models.daos.api.{ AuthTokenDAO, UsuarioDAO }
import models.daos.impl._
import models.services.api.{ AuthTokenService, UsuarioService }
import models.services.impl.{ AuthTokenServiceImpl, UsuarioServiceImpl }
import utils.auth.{ CustomSecuredErrorHandler, CustomUnsecuredErrorHandler, DefaultEnv }

class SilhouetteModule extends AbstractModule with ScalaModule {

  def configure() {
    bind[Silhouette[DefaultEnv]].to[SilhouetteProvider[DefaultEnv]]
    bind[UnsecuredErrorHandler].to[CustomUnsecuredErrorHandler]
    bind[SecuredErrorHandler].to[CustomSecuredErrorHandler]
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[UsuarioDAO].to[UsuarioDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
    bind[UsuarioService].to[UsuarioServiceImpl]
    bind[CacheLayer].to[PlayCacheLayer]
    bind[IDGenerator].toInstance(new SecureRandomIDGenerator())
    bind[PasswordHasher].toInstance(new BCryptPasswordHasher)
    bind[FingerprintGenerator].toInstance(new DefaultFingerprintGenerator(false))
    bind[EventBus].toInstance(EventBus())
    bind[Clock].toInstance(Clock())

    bind[DelegableAuthInfoDAO[PasswordInfo]].to[PasswordInfoDAOImpl]
    bind[DelegableAuthInfoDAO[OAuth1Info]].to[OAuth1InfoDAOImpl]
    bind[DelegableAuthInfoDAO[OAuth2Info]].to[OAuth2InfoDAOImpl]
    bind[DelegableAuthInfoDAO[OpenIDInfo]].to[OpenIDInfoDAOImpl]
  }

  @Provides
  def providerHTTPLayer(
    client: WSClient
  ): HTTPLayer = new PlayHTTPLayer(client)

  @Provides
  def provideEnvironment(
    usuarioService: UsuarioService,
    authenticatorService: AuthenticatorService[CookieAuthenticator],
    eventBus: EventBus
  ): Environment[DefaultEnv] = {
    Environment[DefaultEnv](
      usuarioService,
      authenticatorService,
      Seq(),
      eventBus
    )
  }

  @Provides
  def provideSocialProviderRegistry(
    facebookProvider: FacebookProvider,
    googleProvider: GoogleProvider,
    vkProvider: VKProvider,
    clefProvider: ClefProvider,
    twitterProvider: TwitterProvider,
    xingProvider: XingProvider,
    yahooProvider: YahooProvider
  ): SocialProviderRegistry = {
    SocialProviderRegistry(Seq(
      googleProvider,
      facebookProvider,
      twitterProvider,
      vkProvider,
      xingProvider,
      yahooProvider,
      clefProvider
    ))
  }

  @Provides @Named("oauth1-token-secret-cookie-signer")
  def provideOAuth1TokenSecretCookieSigner(
    configuration: Configuration
  ): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings](
      "silhouette.oauth1TokenSecretProvider.cookie.signer"
    )
    new JcaCookieSigner(config)
  }

  @Provides @Named("oauth1-token-secret-crypter")
  def provideOAuth1TokenSecretCrypter(
    configuration: Configuration
  ): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings](
      "silhouette.oauth1TokenSecretProvider.crypter"
    )
    new JcaCrypter(config)
  }

  @Provides @Named("oauth2-state-cookie-signer")
  def provideOAuth2StageCookieSigner(
    configuration: Configuration
  ): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings](
      "silhouette.oauth2StateProvider.cookie.signer"
    )
    new JcaCookieSigner(config)
  }

  @Provides @Named("authenticator-cookie-signer")
  def provideAuthenticatorCookieSigner(
    configuration: Configuration
  ): CookieSigner = {
    val config = configuration.underlying.as[JcaCookieSignerSettings](
      "silhouette.authenticator.cookie.signer"
    )
    new JcaCookieSigner(config)
  }

  @Provides @Named("authenticator-crypter")
  def provideAuthenticatorCrypter(
    configuration: Configuration
  ): Crypter = {
    val config = configuration.underlying.as[JcaCrypterSettings](
      "silhouette.authenticator.crypter"
    )
    new JcaCrypter(config)
  }

  @Provides
  def provideAuthInfoRepository(
    passwordInfoDAO: DelegableAuthInfoDAO[PasswordInfo],
    oauth1InfoDAO: DelegableAuthInfoDAO[OAuth1Info],
    oauth2InfoDAO: DelegableAuthInfoDAO[OAuth2Info],
    openIDInfoDAO: DelegableAuthInfoDAO[OpenIDInfo]
  ): AuthInfoRepository = {
    new DelegableAuthInfoRepository(
      passwordInfoDAO,
      oauth1InfoDAO,
      oauth2InfoDAO,
      openIDInfoDAO
    )
  }

  @Provides
  def provideAuthenticatorService(
    @Named("authenticator-cookie-signer") cookieSigner: CookieSigner,
    @Named("authenticator-crypter") crypter: Crypter,
    fingerPrintGenerator: FingerprintGenerator,
    idGenerator: IDGenerator,
    configuration: Configuration,
    clock: Clock
  ): AuthenticatorService[CookieAuthenticator] = {
    val config = configuration.underlying.as[CookieAuthenticatorSettings](
      "silhouette.authenticator"
    )
    val encoder = new CrypterAuthenticatorEncoder(crypter)

    new CookieAuthenticatorService(
      config,
      None,
      cookieSigner,
      encoder,
      fingerPrintGenerator,
      idGenerator,
      clock
    )
  }

  @Provides
  def provideAvatarService(
    httpLayer: HTTPLayer
  ): AvatarService = new GravatarService(httpLayer)

  @Provides
  def provideOAuth1TokenSecretProvider(
    @Named("oauth1-token-secret-cookie-signer") cookieSigner: CookieSigner,
    @Named("oauth1-token-secret-crypter") crypter: Crypter,
    configuration: Configuration,
    clock: Clock
  ): OAuth1TokenSecretProvider = {
    val settings = configuration.underlying.as[CookieSecretSettings](
      "silhouette.oauth1TokenSecretProvider"
    )
    new CookieSecretProvider(
      settings,
      cookieSigner,
      crypter,
      clock
    )
  }

  @Provides
  def provideOAuth2StateProvider(
    idGenerator: IDGenerator,
    @Named("oauth2-state-cookie-signer") cookieSigner: CookieSigner,
    configuration: Configuration,
    clock: Clock
  ): OAuth2StateProvider = {
    val settings = configuration.underlying.as[CookieStateSettings](
      "silhouette.oauth2StateProvider"
    )
    new CookieStateProvider(
      settings,
      idGenerator,
      cookieSigner,
      clock
    )
  }

  @Provides
  def providePasswordHasherRegistry(
    passwordHasher: PasswordHasher
  ): PasswordHasherRegistry = {
    new PasswordHasherRegistry(passwordHasher)
  }

  @Provides
  def provideCredentialsProvider(
    authInfoRepository: AuthInfoRepository,
    passwordHasherRegistry: PasswordHasherRegistry
  ): CredentialsProvider = {
    new CredentialsProvider(authInfoRepository, passwordHasherRegistry)
  }

  @Provides
  def provideFacebookProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): FacebookProvider = {
    new FacebookProvider(
      httpLayer,
      stateProvider,
      configuration.underlying.as[OAuth2Settings](
        "silhouette.facebook"
      )
    )
  }

  @Provides
  def provideGoogleProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): GoogleProvider = {
    new GoogleProvider(
      httpLayer,
      stateProvider,
      configuration.underlying.as[OAuth2Settings](
        "silhouette.google"
      )
    )
  }

  @Provides
  def provideVKProvider(
    httpLayer: HTTPLayer,
    stateProvider: OAuth2StateProvider,
    configuration: Configuration
  ): VKProvider = {
    new VKProvider(
      httpLayer,
      stateProvider,
      configuration.underlying.as[OAuth2Settings](
        "silhouette.vk"
      )
    )
  }

  @Provides
  def provideClefProvider(
    httpLayer: HTTPLayer,
    configuration: Configuration
  ): ClefProvider = {
    new ClefProvider(
      httpLayer,
      new DummyStateProvider,
      configuration.underlying.as[OAuth2Settings](
        "silhouette.clef"
      )
    )
  }

  @Provides
  def provideTwitterProvider(
    httpLayer: HTTPLayer,
    tokenSecretProvider: OAuth1TokenSecretProvider,
    configuration: Configuration
  ): TwitterProvider = {
    val settings = configuration.underlying.as[OAuth1Settings](
      "silhouette.twitter"
    )
    new TwitterProvider(
      httpLayer,
      new PlayOAuth1Service(settings),
      tokenSecretProvider,
      settings
    )
  }

  @Provides
  def provideXingProvider(
    httpLayer: HTTPLayer,
    tokenSecretProvider: OAuth1TokenSecretProvider,
    configuration: Configuration
  ): XingProvider = {
    val settings = configuration.underlying.as[OAuth1Settings](
      "silhouette.xing"
    )
    new XingProvider(
      httpLayer,
      new PlayOAuth1Service(settings),
      tokenSecretProvider,
      settings
    )
  }

  @Provides
  def provideYahooProvider(
    cacheLayer: CacheLayer,
    httpLayer: HTTPLayer,
    client: OpenIdClient,
    configuration: Configuration
  ): YahooProvider = {
    val settings = configuration.underlying.as[OpenIDSettings](
      "silhouette.yahoo"
    )
    new YahooProvider(
      httpLayer,
      new PlayOpenIDService(client, settings),
      settings
    )
  }
}
