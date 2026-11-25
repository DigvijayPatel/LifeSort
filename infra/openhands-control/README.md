# LifeSort OpenHands Control Worker

A Cloudflare Worker exposing a Streamable HTTP MCP endpoint for OpenHands Cloud. It is scoped to `DigvijayPatel/LifeSort`.

## Security model

- ChatGPT connects using OAuth 2.1 authorization code + PKCE.
- The Worker login password is configured as a Cloudflare secret; it is not an OpenAI API key.
- OpenHands API credentials are stored only as Cloudflare Worker secrets.
- OAuth tokens, authorization codes and client secrets are stored by the Cloudflare OAuth Provider in KV.
- The repository is fixed in server code. Tool inputs cannot select another repository.
- Never commit `.dev.vars`, API keys, OAuth secrets, or passwords.

## Cloudflare setup

1. Create a KV namespace named `lifesort-openhands-oauth`.
2. Copy its namespace ID into `wrangler.jsonc`, replacing `REPLACE_WITH_KV_NAMESPACE_ID`.
3. Set the Cloudflare Worker build root to `infra/openhands-control`.
4. Use build command `npm install` and deploy command `npx wrangler deploy`.
5. Add Worker secrets:
   - `OPENHANDS_CLOUD_API_KEY`: the API key from OpenHands Cloud.
   - `BRIDGE_LOGIN_PASSWORD`: a unique, high-entropy password for the OAuth login page.
   - `SESSION_SIGNING_SECRET`: a separate random secret of at least 32 bytes.
6. Deploy and test `https://<worker-host>/mcp` with MCP Inspector before connecting ChatGPT.

## Important

The OAuth flow and Worker source must pass a real deployment build and an MCP authorization test before the endpoint is connected to ChatGPT. OpenHands Cloud endpoints are documented in the OpenHands Cloud API guide. The Worker currently uses the V1 app-server API for conversation creation, polling, listing, events, messages and sandbox pause/resume.

## Tools

- `openhands_account`
- `openhands_list_conversations`
- `openhands_start_lifesort_task`
- `openhands_get_start_task`
- `openhands_get_conversation`
- `openhands_send_message`
- `openhands_get_events`
- `openhands_pause_conversation`
- `openhands_resume_conversation`
